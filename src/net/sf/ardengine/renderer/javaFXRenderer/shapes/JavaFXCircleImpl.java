package net.sf.ardengine.renderer.javaFXRenderer.shapes;

import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.javaFXRenderer.IJavaFXGroupable;
import net.sf.ardengine.shapes.IShapeImpl;
import javafx.scene.shape.Circle;

public class JavaFXCircleImpl extends Circle implements IShapeImpl, IJavaFXGroupable {
	
	protected final net.sf.ardengine.shapes.Circle parent;
	
	public JavaFXCircleImpl(net.sf.ardengine.shapes.Circle parent) {
		this.parent = parent;
		setCenterX(parent.getX() - (!parent.isStatic()? Core.cameraX:0));
		setCenterY(parent.getY() - (!parent.isStatic()? Core.cameraY:0));
		setRadius(parent.getRadius());
	}
	
	@Override
	public void coordsChanged() {
		setRadius(parent.getRadius());
	}

	@Override
	public void draw() {
		setCenterX(parent.getX() - (!parent.isStatic()? Core.cameraX:0));
		setCenterY(parent.getY() - (!parent.isStatic()? Core.cameraY:0));
	}

	@Override
	public void groupDraw() {
		setLayoutX(parent.getLayoutX()-getRadius()/2);
		setLayoutY(parent.getLayoutY()-getRadius()/2);
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
	public float getShapeHeight() {
		return (float)getRadius()*2;
	}
	
	@Override
	public float getShapeWidth() {
		return (float)getRadius()*2;
	}
}
