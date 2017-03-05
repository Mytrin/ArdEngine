package net.sf.ardengine.shapes;

import net.sf.ardengine.Core;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.IDrawableImpl;

public class Polygon extends Node implements IShape{

	/**Used by renderer to store object with additional requirements*/
	protected final IShapeImpl implementation;
	/**Coordinates of vertexes*/
	protected float[] coords;

	/**
	 * @param x - X coord of center
	 * @param y - Y coord of center
	 * @param coords points in format [offsetFromX1, offsetFromY1, offsetFromX2, offsetFromY2] accetable by GL_TRIANGLE_STRIP
	 */
	public Polygon(float x, float y, float[] coords) {
		setX(x);
		setY(y);
		this.coords = coords;
		implementation = Core.renderer.createShapeImplementation(this);
	}
	
	/**
	 * Sets new coords of this polygon
	 * @param coords
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
    public ShapeType getType() {
        return ShapeType.POLYGON;
    }

    @Override
	public void draw() {
		implementation.draw();
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
