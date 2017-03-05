package net.sf.ardengine.shapes;

import net.sf.ardengine.Core;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.IDrawableImpl;

public class Rectangle extends Node implements IShape{

	/**Used by renderer to store object with additional requirements*/
	protected final IShapeImpl implementation;
	
	protected float width;
	protected float height;

	/**
	 * 
	 * @param x - x coord
	 * @param y - y coord
	 * @param width - width of this rectangle
	 * @param height - height of this rectangle
	 */
	public Rectangle(float x, float y, float width, float height) {
		setX(x);
		setY(y);
		this.width = width;
		this.height = height;
		implementation = Core.renderer.createShapeImplementation(this);
	}
	
	@Override
	public void draw() {
		implementation.draw();
	}
	
	@Override
	public float getWidth() {
		return width;
	}
	
	public void setWidth(float width) {
		this.width = width;
		implementation.coordsChanged();
	}
	
	@Override
	public float getHeight() {
		return height;
	}
	
	public void setHeight(float height) {
		this.height = height;
		implementation.coordsChanged();
	}

	@Override
	public IDrawableImpl getImplementation() {
		return implementation;
	}
	
	@Override
	public float[] getCoords() {
		return new float[]{getX(), getY(), getX()+width, getY(), getX()+width, getY() + height, getX(), getY()+height};
	}

	@Override
	public ShapeType getType() {
		return ShapeType.RECTANGLE;
	}
}
