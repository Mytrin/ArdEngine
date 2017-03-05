package net.sf.ardengine.renderer.javaFXRenderer.shapes;

import javafx.scene.shape.Polygon;
import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.javaFXRenderer.IJavaFXGroupable;
import net.sf.ardengine.shapes.IShapeImpl;

public class JavaFXPolygonImpl extends Polygon implements IShapeImpl, IJavaFXGroupable {
	
	protected final net.sf.ardengine.shapes.Polygon parent;
	
	public JavaFXPolygonImpl(net.sf.ardengine.shapes.Polygon parent) {
		this.parent = parent;
		setLayoutX(parent.getX() - (!parent.isStatic()? Core.cameraX:0));
		setLayoutY(parent.getY() - (!parent.isStatic()? Core.cameraY:0));
		coordsChanged();
	}
	
	@Override
	public void coordsChanged() {
		getPoints().clear();
		float[] coords = parent.getCoords();
		for(int i=0; i<coords.length; i++){
			getPoints().add((double)coords[i]);
		}
	}

	@Override
	public void draw() {
        setLayoutX(parent.getX() - (!parent.isStatic()? Core.cameraX:0));
        setLayoutY(parent.getY() - (!parent.isStatic()? Core.cameraY:0));
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
		return (float)this.getLayoutBounds().getWidth();
	}
	
	@Override
	public float getShapeHeight() {
		return (float)this.getLayoutBounds().getHeight();
	}
}
