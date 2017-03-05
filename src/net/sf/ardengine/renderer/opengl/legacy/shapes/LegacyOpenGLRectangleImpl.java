package net.sf.ardengine.renderer.opengl.legacy.shapes;

import net.sf.ardengine.shapes.Rectangle;

import static org.lwjgl.opengl.GL11.*;

public class LegacyOpenGLRectangleImpl extends LegacyOpenGLShapeImpl {

    private Rectangle parent;
    private float width;
    private float height;

    public LegacyOpenGLRectangleImpl(Rectangle parent){
        this.parent = parent;
        this.width = parent.getWidth();
        this.height = parent.getHeight();
    }

    @Override
    public Rectangle getParent() {
        return parent;
    }

    @Override
    public void vertexDraw() {
        glBegin(GL_POLYGON);
        glVertex2f(0, 0);
        glVertex2f(width, 0);
        glVertex2f(width, height);
        glVertex2f(0, height);
        glEnd();
    }

    @Override
    public void coordsChanged() {
        width = parent.getWidth();
        height = parent.getHeight();
    }

    @Override
    public float getShapeWidth() {
        return width;
    }

    @Override
    public float getShapeHeight() {
        return height;
    }
}