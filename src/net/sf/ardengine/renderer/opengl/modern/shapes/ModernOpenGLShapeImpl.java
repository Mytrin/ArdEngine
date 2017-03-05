package net.sf.ardengine.renderer.opengl.modern.shapes;


import javafx.scene.paint.Color;
import net.sf.ardengine.renderer.opengl.modern.ModernOpenGLRenderable;
import net.sf.ardengine.renderer.opengl.modern.ModernOpenGLRenderer;
import net.sf.ardengine.shapes.IShape;
import net.sf.ardengine.shapes.IShapeImpl;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

import java.nio.FloatBuffer;

public abstract class ModernOpenGLShapeImpl extends ModernOpenGLRenderable implements IShapeImpl {

    private boolean requiresInit =true;
    private int coordsCount = 0;

    protected abstract IShape getParent();

    protected abstract float[] createCoords();

    @Override
    public void coordsChanged() {
        customBufferInit();
    }

    @Override
    public void draw() {
        if(requiresInit){
            requiresInit = false;
            init(1);
        }

        render(ModernOpenGLRenderer.SHAPE_SHADER, coordsCount);

        postDraw();
    }

    /**
     * Method called after ModernOpenGLRenderable.render() call, to unbind global properties
     */
    public void postDraw(){}

    @Override
    public void update() {
        //everything solved by updateCoords()
    }

    @Override
    public void free() {
        freeBuffers();
    }

    @Override
    protected void customBufferInit() {
        float[] vertexPositions = createCoords();

        coordsCount = vertexPositions.length/4;

        FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length);
        vertexPositionsBuffer.put(vertexPositions);
        vertexPositionsBuffer.flip();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(0));
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexPositionsBuffer, GL15.GL_STATIC_DRAW);
    }

    @Override
    protected void customBind() {
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
    }

    @Override
    protected Color getRenderColor() {
        return getParent().getColor();
    }

    @Override
    protected float getRenderScale() {
        return getParent().getScale();
    }

    @Override
    protected float getRenderAngle() {
        return getParent().getAngle();
    }

    @Override
    protected float getRenderOpacity() {
        return getParent().getOpacity();
    }

    @Override
    protected float getRenderX() {
        return getParent().getX();
    }

    @Override
    protected float getRenderY() {
        return getParent().getY();
    }

    @Override
    protected float getRenderWidth() {
        return getShapeWidth();
    }

    @Override
    protected float getRenderHeight() {
        return getShapeHeight();
    }

    @Override
    protected boolean isRenderStatic() {
        return getParent().isStatic();
    }
}