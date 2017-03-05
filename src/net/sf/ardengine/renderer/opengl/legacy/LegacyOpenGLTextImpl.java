package net.sf.ardengine.renderer.opengl.legacy;

import javafx.scene.paint.Color;
import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.opengl.lib.text.STBFont;
import net.sf.ardengine.renderer.opengl.lib.text.WrappedText;
import net.sf.ardengine.renderer.opengl.lib.textures.Texture;
import net.sf.ardengine.renderer.opengl.lib.textures.TextureFormat;
import net.sf.ardengine.text.IFont;
import net.sf.ardengine.text.ITextImpl;
import net.sf.ardengine.text.Text;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.system.MemoryStack;

import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;

public class LegacyOpenGLTextImpl implements ITextImpl {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    protected final Text parent;

    protected WrappedText calculatedText;

    private final boolean frameBufferMode;
    private boolean requiresRedraw = true;
    private Texture textTexture;

    public LegacyOpenGLTextImpl(Text parent){
        this.parent=parent;
        frameBufferMode = LegacyOpenGLFrameBuffer.isAvailable();
        recalculateSize();
    }


    @Override
    public void draw() {
        if(calculatedText == null || !calculatedText.isSuccess()) recalculateSize();

        if(frameBufferMode){
            if(requiresRedraw) redrawTexture();

            if(textTexture != null){
                int height = (int)Math.ceil(getHeight());
                int width = (int)Math.ceil(getWidth());

                setupGL();
                textTexture.bind();
                glBegin(GL_QUADS);
                {
                    glTexCoord2f(0, 1);
                    glVertex2f(0, 0);
                    glTexCoord2f(0, 0);
                    glVertex2f(0, height);
                    glTexCoord2f(1, 0);
                    glVertex2f(width, height);
                    glTexCoord2f(1, 1);
                    glVertex2f(width, 0);
                }
                glEnd();
                glPopMatrix();
            }

        }else{
            setupGL();
            renderCharacters(false);
        }
    }

    @Override
    public void update() {
        //setupGL() takes care of updates
    }

    private void setupGL(){
        float scale = parent.getScale();
        float opacity = parent.getOpacity();
        float angle = parent.getAngle();
        float x = (float)parent.getX() - (parent.isStatic()?0: Core.cameraX);
        float y = (float)parent.getY() - (parent.isStatic()?0: Core.cameraY);
        float height = (float)getHeight();
        float width = (float)getWidth();
        Color coloring = parent.getColor();

        glPushMatrix();

        // translate to the right location and prepare to draw
        if (scale == 1) {
            glTranslatef(x, y, 0);
        } else {
            glTranslatef(x - width * (scale - 1) / 2, y - height * (scale - 1) / 2, 0);
        }

        glColor4d(coloring.getRed(), coloring.getGreen(), coloring.getBlue(), opacity);

        glScalef(scale, scale, 0);
        glTranslatef(width/2, height/2, 0);
        glRotatef(angle, 0, 0, 1);
        glTranslatef(-width/2, -height/2, 0);
    }

    private void renderCharacters(boolean pushMatrix){
        try ( MemoryStack stack = MemoryStack.stackPush() ) {
            if(!(parent.getFont() instanceof STBFont)) {
                throw new RuntimeException("Selected font is not STB!");
            }

            FloatBuffer xB = stack.floats(0.0f);
            FloatBuffer yB = stack.floats(calculatedText.getYOffset());

            String text = calculatedText.getText();

            STBFont font = (STBFont)parent.getFont();
            STBTTAlignedQuad quad = STBTTAlignedQuad.mallocStack(stack);

            font.bind();

            if(pushMatrix) glPushMatrix();
            glTranslatef(0, 0, 0);
            glBegin(GL_QUADS);
            for ( int i = 0; i < text.length(); i++ ) {
                char c = text.charAt(i);

                if (c == '\n') {
                    yB.put(0, yB.get(0) + parent.getFont().getFontHeight());
                    xB.put(0, 0.0f);
                    continue;
                }

                font.activateChar(xB,yB,quad, c);

                glTexCoord2f(quad.s0(),quad.t0());
                glVertex2f(quad.x0(),quad.y0());
                glTexCoord2f(quad.s1(),quad.t0());
                glVertex2f(quad.x1(),quad.y0());
                glTexCoord2f(quad.s1(),quad.t1());
                glVertex2f(quad.x1(),quad.y1());
                glTexCoord2f(quad.s0(),quad.t1());
                glVertex2f(quad.x0(),quad.y1());
            }
            glEnd();
            glPopMatrix();
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Rendering text", e);
        }
    }

    private void redrawTexture(){
        int fbWidth = (int)Math.ceil(getWidth());
        int fbHeight = (int)Math.ceil(getHeight());

        if(fbWidth <=0 || fbHeight <=0){
            if(textTexture != null){
                textTexture.free();
                textTexture = null;
            }
            requiresRedraw = false;
            return;
        }

        LegacyOpenGLFrameBuffer fb = new LegacyOpenGLFrameBuffer(fbWidth, fbHeight, TextureFormat.ALPHA_B);

        glDisable(GL_BLEND); //Text would be blended twice otherwise

        fb.bind();
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT);

        glColor4f(1, 1, 1, 1);
        renderCharacters(true);

        textTexture = fb.surrenderTexture();

        glEnable(GL_BLEND);

        requiresRedraw = false;
    }

    @Override
    public void free() {
        if(textTexture != null){
            textTexture.free();
        }
    }

    private void recalculateSize(){
        calculatedText = new WrappedText(parent.getText(), (STBFont)parent.getFont(), parent.getWrapWidth());
        calculatedText.calculate();

        if(frameBufferMode) requiresRedraw = true;
    }

    @Override
    public void textChanged(String newText) {
        recalculateSize();
    }

    @Override
    public void wrapWidthChanged(int newWrapWidth) {
        recalculateSize();
    }

    @Override
    public void fontChanged(IFont newFont) {
        recalculateSize();
    }

    @Override
    public float getWidth() {
        if(calculatedText == null) return -1;

        return calculatedText.getWidth();
    }
    @Override
    public float getHeight() {
        if(calculatedText == null) return -1;

        return calculatedText.getHeight();
    }

}