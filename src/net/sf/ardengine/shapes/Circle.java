package net.sf.ardengine.shapes;

import net.sf.ardengine.Core;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.IDrawableImpl;

public class Circle extends Node implements IShape{

	/**Used by renderer to store object with additional requirements*/
	protected final IShapeImpl implementation;

	protected float radius = 0;

	/**
	 * @param centerX - X coord of center
	 * @param centerY - Y coord of center
	 * @param radius radius of this circle
	 */
	public Circle(float centerX, float centerY, float radius) {
		this.radius = radius;
        setX(centerX);
        setY(centerY);
		implementation = Core.renderer.createShapeImplementation(this);
	}
	
	@Override
	public float[] getCoords() {
		return new float[]{radius};
	}

	@Override
	public ShapeType getType() {
		return ShapeType.CIRCLE;
	}

	/**
	 * Changes radius of this circle
	 * @param radius - new Radius
	 */
	public void setRadius(float radius) {
		this.radius = radius;
		implementation.coordsChanged();
	}

	/**
	 * @return radius of this circle
	 */
	public float getRadius() {
		return radius;
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
		return radius*2;
	}
	
	@Override
	public float getHeight() {
		return radius*2;
	}

}
