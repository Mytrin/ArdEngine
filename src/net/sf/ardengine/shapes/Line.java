package net.sf.ardengine.shapes;

import net.sf.ardengine.Core;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.IDrawableImpl;

public class Line extends Node implements IShape{

    /**Used by renderer to store object with additional requirements*/
    protected final IShapeImpl implementation;
    /**Width of line*/
    protected float strokeWidth = 1.0f;
    /**Coordinates of vertexes*/
    protected float[] coords;

    /**
     * @param x - X coord of line start
     * @param y - Y coord of line start
     * @param coords points in format [offsetFromX1, offsetFromY1, offsetFromX2, offsetFromY2] accetable by GL_TRIANGLE_STRIP
     */
    public Line(float x, float y, float[] coords) {
        setX(x);
        setY(y);
        this.coords = coords;
        implementation = Core.renderer.createShapeImplementation(this);
    }

    @Override
    public ShapeType getType() {
        return ShapeType.LINES;
    }

    /**
     * Sets new vertexes of this line
     * @param coords new vertex coords in [x1, y1, x2, y2] format
     */
    public void setCoords(float[] coords) {
        this.coords = coords;
        implementation.coordsChanged();
    }

    @Override
    public float[] getCoords() {
        return coords;
    }

    @Override
    public void draw() {
        implementation.draw();
    }

    /**
     * @return width of line
     */
    public float getStrokeWidth() {
        return strokeWidth;
    }

    /**
     * @param strokeWidth new width of line, should not exceed 10.0f
     */
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = strokeWidth;
    }

    @Override
    public IDrawableImpl getImplementation() {
        return implementation;
    }

    @Override
    public float getWidth() {
        return implementation.getShapeWidth();
    }

    @Override
    public float getHeight() {
        return implementation.getShapeHeight();
    }

}