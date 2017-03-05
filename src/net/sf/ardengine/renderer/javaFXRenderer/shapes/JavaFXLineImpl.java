package net.sf.ardengine.renderer.javaFXRenderer.shapes;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.javaFXRenderer.IJavaFXGroupable;
import net.sf.ardengine.shapes.IShapeImpl;

import java.util.List;

public class JavaFXLineImpl extends Path implements IShapeImpl, IJavaFXGroupable {

    protected final net.sf.ardengine.shapes.Line parent;

    public JavaFXLineImpl(net.sf.ardengine.shapes.Line parent) {
        this.parent = parent;
        setLayoutX(parent.getX() - (!parent.isStatic()? Core.cameraX:0));
        setLayoutY(parent.getY() - (!parent.isStatic()? Core.cameraY:0));
        coordsChanged();
    }

    @Override
    public void coordsChanged() {
        float[] coords = parent.getCoords();

        List<PathElement> elements = getElements();
        elements.clear();

        if(coords.length < 2) return;

        elements.add(new MoveTo(coords[0],coords[1]));
        for(int i=2; i<coords.length; i+=2){
            elements.add(new LineTo(coords[i], coords[i+1]));
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
        setStroke(parent.getColor());
        setStrokeWidth(parent.getStrokeWidth());
    }

    @Override
    public void free() {
        getElements().clear();
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
