package net.sf.ardengine.text;

import javafx.scene.paint.Color;
import net.sf.ardengine.Core;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.IDrawableImpl;

public class Text extends Node {
	/**Content of Text*/
	protected String text;
	/**Used font*/
	protected IFont font;
	/**Maximal length of line */
	protected int wrapWidth = -1;

	/**Used by renderer to store object with additional requirements*/
	private final ITextImpl implementation;
	
	public Text(String text, IFont font, int wrapWidth) {
		this(text, font);
		this.wrapWidth = wrapWidth;
	}
	
	public Text(String text, IFont font) {
		this.text = text;
		this.font = font;
		this.color = Color.BLACK;
		implementation = Core.renderer.createTextImplementation(font, this);
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
	public void updateLogic() {
		super.updateLogic();
	}
	
   /**
    * Changes content of this Text 
    * @param text  new content 
    */
	public void setText(String text) {
		this.text = text;
		implementation.textChanged(text);
		updateCollisions();
	}

	@Override
	public void setColor(javafx.scene.paint.Color color) {
		this.color = color;
		implementation.colorChanged(color);
	}

	/**
	 * @return content of this Text 
	 */
	public String getText() {
		return text;
	}
	
   /**
   * Changes Font of this Text 
   * @param font   new font
   **/
	public void setFont(IFont font){
		this.font = font;
		implementation.fontChanged(font);
		updateCollisions();
	}
	
	/**
	 * @return used font
	 */
	public IFont getFont() {
		return font;
	}

	/**
	 * @return Maximal length of line (-1 == INFINITE)
	 */
	public int getWrapWidth() {
		return wrapWidth;
	}
	
	/**
	 * Changes maximal length of line
	 * @param wrapWidth -1 == INFINITE
	 */
	public void setWrapWidth(int wrapWidth) {
		this.wrapWidth = wrapWidth;
		implementation.wrapWidthChanged(wrapWidth);
		updateCollisions();
	}
	
	@Override
	public float getWidth() {
		return implementation.getWidth();
	}
	
	@Override
	public float getHeight() {
		return implementation.getHeight();
	}
}
