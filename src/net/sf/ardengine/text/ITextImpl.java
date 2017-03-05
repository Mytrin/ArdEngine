package net.sf.ardengine.text;

import javafx.scene.paint.Color;
import net.sf.ardengine.renderer.IDrawableImpl;

public interface ITextImpl extends IDrawableImpl{

    /**
     * Called by text when its font has been changed
     * @param newText - new text
     */
    public void textChanged(String newText);

	/**
	 * Called by text when its font has been changed
	 * @param newFont - new font
	 */
	public void fontChanged(IFont newFont);

	/**
	 * Called by text when its wrapping width has been changed
	 * @param newWrapWidth - new maximal line length
	 */
	public void wrapWidthChanged(int newWrapWidth);
	
	/**
	 * @return Total width of this Text
	 */
	public abstract float getWidth();
	
	/**
	 * @return Total height of this Text
	 */
	public abstract float getHeight();
}
