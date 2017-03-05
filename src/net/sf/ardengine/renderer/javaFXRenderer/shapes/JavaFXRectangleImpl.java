package net.sf.ardengine.renderer.javaFXRenderer.shapes;

import javafx.scene.shape.Rectangle;

import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.javaFXRenderer.IJavaFXGroupable;
import net.sf.ardengine.shapes.IShapeImpl;

public class JavaFXRectangleImpl extends Rectangle implements IShapeImpl, IJavaFXGroupable {
	
	protected final net.sf.ardengine.shapes.Rectangle parent;
	
	public JavaFXRectangleImpl(net.sf.ardengine.shapes.Rectangle parent) {
		this.parent = parent;
		setX(parent.getX() - (!parent.isStatic()? Core.cameraX:0));
		setY(parent.getY() - (!parent.isStatic()? Core.cameraY:0));
		setWidth(parent.getWidth());
		setHeight(parent.getHeight());
	}
	
	@Override
	public void coordsChanged() {
		setWidth(parent.getWidth());
		setHeight(parent.getHeight());
	}

	@Override
	public void draw() {
		setX(parent.getX() - (!parent.isStatic()? Core.cameraX:0));
		setY(parent.getY() - (!parent.isStatic()? Core.cameraY:0));
	}

    @Override
    public void groupDraw() {
        setLayoutX(parent.getLayoutX());
        setLayoutY(parent.getLayoutY());
    }

    @Override
    public void update() {
        setRotate(parent.getAngle());
        setOpacity(parent.getOpacity());
        setScaleX(parent.getScale());
        setScaleY(parent.getScale());
        setFill(parent.getColor());
    }

    @Override
	public void free() {
		//nothing to do
	}

	@Override
	public float getShapeWidth() {
		return (float)getWidth();
	}

	@Override
	public float getShapeHeight() {
		return (float)getHeight();
	}
}
