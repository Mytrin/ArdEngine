package net.sf.ardengine.renderer.javaFXRenderer;


import javafx.scene.Group;
import net.sf.ardengine.Core;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.IGroupImpl;

//Using JavaFX Group, which is much better, but not usable
//for other other renderers.
public class JavaFXGroupImpl extends Group implements IGroupImpl, IJavaFXGroupable {

    private net.sf.ardengine.Group parentGroup;

    public JavaFXGroupImpl(net.sf.ardengine.Group parentGroup){
        super();
        this.parentGroup = parentGroup;
    }

    @Override
    public void groupDraw() {
        setLayoutX(parentGroup.getLayoutX());
        setLayoutY(parentGroup.getLayoutY());

        drawChildren();
    }

    @Override
    public void draw() {
        setLayoutX(parentGroup.getX()- (!parentGroup.isStatic()? Core.cameraX:0));
        setLayoutY(parentGroup.getY()- (!parentGroup.isStatic()? Core.cameraY:0));

        drawChildren();
    }

    private void drawChildren(){
        for (javafx.scene.Node child : getChildren()){
            ((IJavaFXGroupable)child).groupDraw();
        }
    }

    @Override
    public void update() {}

    @Override
    public void free() {
        getChildren().clear();
    }

    @Override
    public void childAdded(Node child) {
        if(!(child.getImplementation() instanceof IJavaFXGroupable)) throw new RuntimeException("FXNode is not groupable!");
        getChildren().add((javafx.scene.Node)child.getImplementation());
    }

    @Override
    public void childRemoved(Node child) {
        getChildren().remove(child.getImplementation());
    }
}
