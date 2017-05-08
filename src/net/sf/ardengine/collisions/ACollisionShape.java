package net.sf.ardengine.collisions;

import javafx.scene.paint.Color;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.util.IRenderableCollision;

/**
 * Basic ACollisionShape class uniting all methods, which collision shapes should
 * have.
 * 
 * @author mytrin
 */
public abstract class ACollisionShape implements IRenderableCollision {
	/** Indicator if auto detect node scale */
	protected boolean withScale = false;
	/** Variable, where is stored actual node scale */
	protected float aktScale = 1;

	/**
	 * @param cs target shape which may collide
	 * @return true if both this and target shape share space
	 */
	public abstract boolean isColliding(ACollisionShape cs);

	/**
	 * Updates shape's coords based on node's coords(called automatically by
	 * Node).
	 */
	public abstract void updateProperties();

	/**
	 * @return true if shape counts with scale
	 * */
	public boolean testsWithScale() {
		return withScale;
	}

	/** @param test true if shape should count with node scale  */
	public void testWithScale(boolean test) {
		withScale = test;
	}

	/**
	 * @return Coords of this shape
	 */
	public abstract float[] getActCoords();

	/**
	 * @param vx X coord of first point
	 * @param vy Y coord of first point
	 * @param wx X coord of second point
	 * @param wy Y coord of second point
	 * @return distance of given points
	 */
	public static double distOfTwo(float vx, float vy, float wx, float wy) {
		return (Math.pow(vx - wx, 2) + Math.pow(vy - wy, 2));
	}

	/**
	 * @param vx X coord of first point
	 * @param vy Y coord of first point
	 * @param wx X coord of second point
	 * @param wy Y coord of second point
	 * @return distance of given points
	 */
	public static double distOfTwo(float vx, float vy, double wx, double wy) {
		return (Math.pow(vx - wx, 2) + Math.pow(vy - wy, 2));
	}

	/**
	 * @param ax X coord of first point of line segment
	 * @param ay Y coord of first point of line segment
	 * @param bx X coord of second point of line segment
	 * @param by Y coord of second point of line segment
	 * @param centerX X coord of point
	 * @param centerY Y coord of point
	 * @return distance between point and line segment
	 */
	protected double distToSegment(float ax, float ay, float bx, float by,
			float centerX, float centerY) {
		double l2 = distOfTwo(ax, ay, bx, by);
		if (l2 == 0) {
			return Math.sqrt((distOfTwo(centerX, centerY, ax, ay)));
		}
		double t = ((centerX - ax) * (bx - ax) + (centerY - ay) * (by - ay))
				/ l2;
		if (t < 0) {
			return Math.sqrt(distOfTwo(centerX, centerY, ax, ay));
		} else if (t > 1) {
			return Math.sqrt(distOfTwo(centerX, centerY, bx, ay));
		}
		return Math.sqrt(distOfTwo(centerX, centerY, ax + t * (bx - ax), ay + t
				* (by - ay)));
	}

	//DEBUG
	@Override
	public Color getLineColor() {
		return Color.RED;
	}
}