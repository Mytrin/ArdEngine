package net.sf.ardengine.renderer.opengl.modern;

import javafx.scene.paint.Color;
import net.sf.ardengine.renderer.opengl.lib.shader.Matrix;
import net.sf.ardengine.renderer.opengl.lib.shader.Shader;
import net.sf.ardengine.renderer.opengl.lib.shader.Uniform;
import net.sf.ardengine.renderer.opengl.lib.text.STBFont;
import net.sf.ardengine.renderer.opengl.lib.text.WrappedText;
import net.sf.ardengine.renderer.opengl.lib.textures.Texture;
import net.sf.ardengine.text.IFont;
import net.sf.ardengine.text.ITextImpl;
import net.sf.ardengine.text.Text;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_BLEND;

public class ModernOpenGLTextImpl extends ModernOpenGLRenderable implements ITextImpl {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    protected final Text parent;
    protected boolean redraw = true;
    protected boolean init = true;

    protected WrappedText calculatedText;
    protected Texture renderedText;

    protected final static float[] textureUVCoordinates = {
            0f,  1f,
            0f,  0f,
            1f,  1f,
            1f,  0f
    };
    protected final static FloatBuffer textureUVCoordinatesBuffer = BufferUtils.createFloatBuffer(textureUVCoordinates.length);
    static{
        textureUVCoordinatesBuffer.put(textureUVCoordinates);
        textureUVCoordinatesBuffer.flip();
    }

    protected FloatBuffer redrawCoords;
    protected FloatBuffer redrawTexCoords;

    public ModernOpenGLTextImpl(Text parent){
        this.parent=parent;
        recalculateSize();
    }

    @Override
    protected void customBufferInit() {
        if(!redraw){
            //Rendering FB texture
            float hWidth = (float)getRenderWidth()/2f;
            float hHeight = (float)getRenderHeight()/2f;

            float[] vertexPositions = new float[]{
                    -hWidth,  -hHeight,    0f, 1f,
                    -hWidth,   hHeight,    0f, 1f,
                    hWidth,  -hHeight,    0f, 1f,
                    hWidth,   hHeight,    0f, 1f
            };

            FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length);
            vertexPositionsBuffer.put(vertexPositions);
            vertexPositionsBuffer.flip();

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(0));
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexPositionsBuffer, GL15.GL_STATIC_DRAW);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(1));
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureUVCoordinatesBuffer, GL15.GL_STATIC_DRAW);
        }else{
            if(redrawCoords == null) return;

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(0));
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, redrawCoords, GL15.GL_STATIC_DRAW);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(1));
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, redrawTexCoords, GL15.GL_STATIC_DRAW);

        }
    }

    @Override
    protected void customBind() {
            //Rendering FB texture
            GL20.glEnableVertexAttribArray(0);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(0));
            GL20.glVertexAttribPointer(
                    0,                 	//must match the layout in the shader!
                    4,                 	// size
                    GL11.GL_FLOAT,     	// type
                    false,           		// normalized?
                    0,                  	// stride
                    0            			// array buffer offset
            );

            GL20.glEnableVertexAttribArray(1);
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(1));
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

            if(!redraw){
                renderedText.bind(); //else font texture is bound
            }
    }

    @Override
    protected void update(Shader shader) {
        super.update(shader);
        if(!redraw){
            ((Uniform.Matrix)shader.getUniform("ortoMatrix")).setValue(ModernOpenGLRenderer.getViewMatrix());
        }else{
            //rendering to FB at [0;0]
            Matrix fbMatrix = Matrix.buildOrtographicMatrix((float)getRenderWidth(), (float)getRenderHeight());
            ((Uniform.Matrix)shader.getUniform("ortoMatrix")).setValue(fbMatrix);

            ((Uniform.Matrix)shader.getUniform("transformMatrix")).setValue(Matrix.IDENTITY_MATRIX4x4);
            ((Uniform.Matrix)shader.getUniform("rotationMatrix")).setValue(Matrix.IDENTITY_MATRIX4x4);
        }
    }

    @Override
    public void draw() {
        if(init){
            init(2); //2 custom buffer - vertices + UV
            init = false;
        }
        if(redraw){
            updateTexture();
            redraw = false;
            customBufferInit();
        }

        if(renderedText != null){
            render(ModernOpenGLRenderer.SPRITE_SHADER);
        }
    }

    public void updateTexture(){
        if(calculatedText == null || !calculatedText.isSuccess()) recalculateSize();

        try ( MemoryStack stack = MemoryStack.stackPush() ) {
            if(!(parent.getFont() instanceof STBFont)) {
                throw new RuntimeException("Selected font is not STB!");
            }

            FloatBuffer x = stack.floats(0.0f);
            FloatBuffer y = stack.floats(calculatedText.getYOffset());

            String text = calculatedText.getText();

            float[] coords;
            float[] texCoords;

            STBFont font = (STBFont)parent.getFont();
            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);

            int fbWidth = (int)Math.ceil(getRenderWidth());
            int fbHeight = (int)Math.ceil(getRenderHeight());

            if(fbWidth <=0 || fbHeight <=0){
                renderedText = null;
                if(renderedText!=null){
                    renderedText.free();
                }
                return;
            }

            ModernOpenGLFrameBuffer fb = new ModernOpenGLFrameBuffer(fbWidth, fbHeight);

            glDisable(GL_BLEND); //Text would be blended twice otherwise

            fb.bind();

            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT);

            for ( int i = 0; i < text.length(); i++ ) {
                char c = text.charAt(i);

                if (c == '\n') {
                    y.put(0, y.get(0) + parent.getFont().getFontHeight());
                    x.put(0, 0.0f);
                    continue;
                }

                font.activateChar(x,y,quad, c);

                coords = new float[]{
                        quad.x0(), quad.y0(), 0, 1,
                        quad.x1(), quad.y0(), 0, 1,
                        quad.x0(), quad.y1(), 0, 1,
                        quad.x1(), quad.y1(), 0, 1
                };

                texCoords = new float[]{
                        quad.s0(), quad.t0(),
                        quad.s1(), quad.t0(),
                        quad.s0(), quad.t1(),
                        quad.s1(), quad.t1()
                };

                redrawCoords = BufferUtils.createFloatBuffer(coords.length);
                redrawCoords.put(coords);
                redrawCoords.flip();
                redrawTexCoords = BufferUtils.createFloatBuffer(texCoords.length);
                redrawTexCoords.put(texCoords);
                redrawTexCoords.flip();

                //Not very happy with rendering each character separately
                //at least it goes to FB
                customBufferInit();

                font.bind();

                render(ModernOpenGLRenderer.TEXT_SHADER);

            }

            glEnable(GL_BLEND);

            if(renderedText!=null){
                renderedText.free();
            }

            renderedText = fb.surrenderTexture();
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Rendering text", e);
        }
    }

    @Override
    public void update() {
        //everything solved by specificic *Changed()
    }

    @Override
    public void free() {
        freeBuffers();
        if(renderedText != null) renderedText.delete();
    }

    private void recalculateSize(){
        calculatedText = new WrappedText(parent.getText(), (STBFont)parent.getFont(), parent.getWrapWidth());
        calculatedText.calculate();
    }

    @Override
    public void textChanged(String newText) {
        recalculateSize();
        redraw = true;
    }

    @Override
    public void colorChanged(Color newColor) {
        redraw = true;
    }

    @Override
    public void wrapWidthChanged(int newWrapWidth) {
        recalculateSize();
        redraw = true;
    }

    @Override
    public void fontChanged(IFont newFont) {
        recalculateSize();
        redraw = true;
    }

    @Override
    public float getWidth() {
        if(calculatedText == null) recalculateSize();

        return calculatedText.getWidth();
    }
    @Override
    public float getHeight() {
        if(calculatedText == null) recalculateSize();

        return calculatedText.getHeight();
    }

    @Override
    protected float getRenderAngle() {
        return parent.getAngle();
    }

    @Override
    protected float getRenderScale() {
        return parent.getScale();
    }

    @Override
    protected float getRenderOpacity() {
        return parent.getOpacity();
    }

    @Override
    protected float getRenderX() {
        if(redraw) return 0;

        return parent.getX();
    }

    @Override
    protected float getRenderY() {
        if(redraw) return 0;

        return parent.getY();
    }

    @Override
    protected float getRenderWidth() {
        return getWidth();
    }

    @Override
    protected float getRenderHeight() {
        return getHeight();
    }

    @Override
    protected Color getRenderColor() {
        if(redraw){
            return parent.getColor();
        }
        return Color.WHITE; //sprite shader would make everything black otherwise
    }

    @Override
    protected boolean isRenderStatic() {
        return parent.isStatic();
    }
}