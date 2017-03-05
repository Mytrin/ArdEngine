package net.sf.ardengine.renderer;

/**
 * Implementation of nodes for custom renderers
 * 
 * @author mytrin
 *
 */
public interface IDrawableImpl {

	/**
	 * Called from node by renderer
	 */
	public void draw();

	/**
	 * Notification from parent object, that it changed basic transform values
	 */
	public void update();

    /**
     * Called from node by renderer
     */
	public void free();

}