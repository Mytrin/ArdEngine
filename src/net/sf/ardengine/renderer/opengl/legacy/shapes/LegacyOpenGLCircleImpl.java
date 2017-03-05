package net.sf.ardengine.renderer.opengl.legacy.shapes;

import net.sf.ardengine.shapes.Circle;


import static org.lwjgl.opengl.GL11.*;

public class LegacyOpenGLCircleImpl extends LegacyOpenGLShapeImpl{
    //TODO configurable
    private static final int SMOOTHNESS = 36;

    private Circle parent;

    private float radius = 0;

    private float[] coords;

    public LegacyOpenGLCircleImpl(Circle parent){
        this.parent = parent;
        this.radius = parent.getRadius();
        coordsChanged();
    }

    @Override
    public void coordsChanged() {
        coords = new float[(SMOOTHNESS+1)*2];

        for(int i = 0; i <= SMOOTHNESS; i++){ //36 = smoothness of circle
            double angle = Math.PI * 2 * i / 18;
            coords[i*2]=(float)(Math.cos(angle)*radius);
            coords[i*2+1]=(float)(Math.sin(angle)*radius);
        }
    }

    @Override
    public Circle getParent() {
        return parent;
    }

    @Override
    public void vertexDraw() {
        glBegin(GL_TRIANGLE_FAN);
        glVertex2f(0, 0);
        for(int i = 0; i <= 36; i++){ //36 = smoothness of circle
            glVertex2f(coords[i*2], coords[i*2+1]);
        }
        glEnd();
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
