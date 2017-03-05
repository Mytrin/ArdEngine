package net.sf.ardengine.renderer.opengl.modern.shapes;

import net.sf.ardengine.shapes.IShape;
import net.sf.ardengine.shapes.Rectangle;

public class ModernOpenGLRectangleImpl extends ModernOpenGLShapeImpl{

    private Rectangle parent;
    private float width;
    private float height;

    public ModernOpenGLRectangleImpl(Rectangle parent){
        this.parent = parent;
        this.width = parent.getWidth();
        this.height = parent.getHeight();
    }

    @Override
    protected float[] createCoords() {
        width = parent.getWidth();
        height = parent.getHeight();

        float hWidth = width/2;
        float hHeight = height/2;

        return new float[]{
                -hWidth,   -hHeight,  0,   1,
                -hWidth,    hHeight,  0,   1,
                 hWidth,   -hHeight,  0,   1,
                 hWidth,    hHeight,  0,   1,
        };
    }

    @Override
    public IShape getParent() {
        return parent;
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