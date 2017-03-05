package net.sf.ardengine.renderer.opengl.modern.shapes;


import net.sf.ardengine.shapes.Circle;
import net.sf.ardengine.shapes.IShape;
import org.lwjgl.opengl.GL11;

public class ModernOpenGLCircleImpl extends ModernOpenGLShapeImpl{

    //TODO configurable
    private static final int SMOOTHNESS = 36;

    private Circle parent;

    private float radius = 0;

    public ModernOpenGLCircleImpl(Circle parent){
        this.parent = parent;
        this.radius = parent.getRadius();
    }

    @Override
    protected float[] createCoords() {
        radius = parent.getRadius();

        float[] coords = new float[SMOOTHNESS*4];

        for(int i=0;  i < SMOOTHNESS;  i++){
            double theta = 2*Math.PI/SMOOTHNESS*i;

            coords[i*4] = (float)(radius*Math.cos(theta) - radius);
            coords[i*4+1] = (float)(radius*Math.sin(theta) -radius);
            coords[i*4+2] = 0;
            coords[i*4+3] = 1;
        }

        return coords;
    }

    @Override
    protected int getDrawMode() {
       return GL11.GL_TRIANGLE_FAN;
    }

    @Override
    public IShape getParent() {
        return parent;
    }

    @Override
    public float getShapeWidth() {
        return radius*2;
    }

    @Override
    public float getShapeHeight() {
        return radius*2;
    }
}