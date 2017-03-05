package net.sf.ardengine.renderer.javaFXRenderer;

import net.sf.ardengine.Core;
import net.sf.ardengine.text.IFont;
import net.sf.ardengine.text.ITextImpl;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class JavaFXTextImpl extends Text implements ITextImpl, IJavaFXGroupable{
	
	private final net.sf.ardengine.text.Text parentText;
	
	public JavaFXTextImpl(Font font, net.sf.ardengine.text.Text text) {
		super(text.getX(), text.getY(), text.getText());
		setFont(font);
		setWrappingWidth(text.getWrapWidth());
		parentText = text;
	}
	
	@Override
	public void draw() {
        setX(parentText.getX() - (!parentText.isStatic()? Core.cameraX:0));
        //coordinates are from different corner
		setY(parentText.getY() - (!parentText.isStatic()? Core.cameraY:0) - getFont().getSize());
	}

	@Override
	public void groupDraw() {
		setLayoutX(parentText.getLayoutX());
		//coordinates are from different corner
		setLayoutY(parentText.getLayoutY() + getFont().getSize());
	}

    @Override
    public void update() {
        setTranslateZ(parentText.getZ());
        setRotate(parentText.getAngle());
        setOpacity(parentText.getOpacity());
        setScaleX(parentText.getScale());
        setScaleY(parentText.getScale());
        setFill(parentText.getColor());
    }

    @Override
	public void free() {
		//nothing to do
	}

	@Override
	public void textChanged(String newText) {
		setText(newText);
	}

	@Override
	public void fontChanged(IFont newFont) {
		setFont(((JavaFXFont)newFont).getFxFont());
	}

	@Override
	public void wrapWidthChanged(int newWrapWidth) {
		setWrappingWidth(newWrapWidth);	
	}
	
	@Override
	public float getWidth() {
		return (float)this.getLayoutBounds().getWidth();
	}
	
	@Override
	public float getHeight() {
		return (float)this.getLayoutBounds().getHeight();
	}
}