package net.sf.ardengine.shapes;

import net.sf.ardengine.renderer.IDrawableImpl;

public interface IShapeImpl extends IDrawableImpl {
	
	/**
	 *  automatically called, when shape coords have been altered(Circle - changed radius)
	 */
	public void coordsChanged();

	/**
	 * @return Total width of this Shape
	 */
	public float getShapeWidth();
	
	/**
	 * @return Total height of this Shape
	 */
	public float getShapeHeight();
}
