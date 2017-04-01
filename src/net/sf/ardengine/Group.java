package net.sf.ardengine;

import javafx.scene.paint.Color;
import net.sf.ardengine.renderer.IDrawableImpl;
import net.sf.ardengine.renderer.IGroupImpl;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Group acts as it would be one Node, however it unites more nodes under one
 * system, so it's useful for layouts and things consisting from
 * parts.
 *
 * Nodes added to group will be automatically added to game drawable list!
 */
public class Group extends Node{
    /**List of current children*/
    private final List<Node> children = new LinkedList<>();

    /**Current width of group content*/
    protected float width = 0;
    /**Current height of group content*/
    protected float height = 0;

    /**Renderer specific implementation*/
    private final IGroupImpl implementation;

    /**
     * Group acts as it would be one Node, however it unites more nodes under one
     * system.
     */
    public Group(){
        implementation = Core.renderer.createGroupImplementation(this);
    }

    /**
     * Invokes given method on each child
     * @param action action called on children
     */
    public void forEachChildren(Consumer<Node> action){
        //safer than forEach
        for(int i=0; i < children.size(); i++){
            action.accept(children.get(i));
        }
    }

    /**
     * Adds given children to group and THEN updates group size
     * @param childrenToAdd new Nodes to append to current ones
     */
    public void addChildren(Node[] childrenToAdd){
        for(Node child : childrenToAdd){
            if(child == null) continue;

            children.add(child);
            implementation.childAdded(child);
        }
        recalculateSize();
    }

    /**
     * Adds given child to group and updates group size
     * @param child new Node to append to current ones
     */
    public void addChild(Node child){
        if(child == null) return;
        children.add(child);
        implementation.childAdded(child);
        recalculateSize();
    }

    /**
     * Removes given children from group and THEN updates group size
     * @param childrenToRemove Nodes to remove from current ones
     */
    public void removeChildren(Node[] childrenToRemove){
        for(Node child : childrenToRemove){
            if(child == null) continue;
            children.remove(child);
            implementation.childRemoved(child);
        }
        recalculateSize();
    }

    /**
     * Removes given child from group and updates group size
     * @param child Node to remove from current ones
     */
    public void removeChild(Node child){
        removeChild(child, true);
    }

    /**
     * Removes given child from group and updates group size
     * @param child Node to remove from current ones
     * @param removePermanently true, if renderer should free allocated resources for this child
     */
    public void removeChild(Node child, boolean removePermanently){
        if(child == null) return;

        children.remove(child);

        if(removePermanently){
            implementation.childRemoved(child);
        }

        recalculateSize();
    }

    /**
     * @return Duplicate list of group Children
     */
    public List<Node> getChildren() {
        LinkedList<Node> copyChildren = new LinkedList<>();
        children.forEach((Node node) -> copyChildren.add(node));

        return copyChildren;
    }

    /**
     * Removes all nodes from group and updates group size
     */
    public void clearChildren(){
        children.forEach((child)->  implementation.childRemoved(child));

        children.clear();

        recalculateSize();
    }

    @Override
    public void draw() {
        children.forEach((child)->{ //Update coordinates
            child.setX(getX()+child.getLayoutX());
            child.setY(getY()+child.getLayoutY());
        });
        implementation.draw();
    }

    @Override
    public void setScale(float newScale) {
        super.setScale(newScale);
        children.forEach((child)->{child.setScale(newScale);});
    }

    @Override
    public void setOpacity(float newOpacity) {
        super.setOpacity(newOpacity);
        children.forEach((child)->{child.setOpacity(newOpacity);});
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        children.forEach((child)->{child.setColor(color);});
    }

    @Override
    public void setAngle(float angle) {
        //TODO fix
        //some drawables might not have been initialized yet, skip a frame
        if(width < 1 || height < 1){
            delayAction(()->{
                setAngle(angle);
            });
        }

        super.setAngle(angle);

        recalculateSize();

        float centerX = width/2;
        float centerY = height/2;

        children.forEach((child)->{
            child.setAngle(angle);

            float layoutX = child.getLayoutX();
            float layoutY = child.getLayoutY();

            float angleSin = (float)Math.sin(angle * Math.PI / 180);
            float angleCos = (float)Math.cos(angle * Math.PI / 180);

            // origin
            layoutX -= centerX;
            layoutY -= centerY;

            // rotate point
            float layoutRotateX = layoutX * angleCos - layoutY * angleSin;
            float layoutRotateY = layoutX * angleSin + layoutY * angleCos;

            // and back
            layoutRotateX += centerX;
            layoutRotateY += centerY;

            child.setLayoutOffsetCoords(layoutRotateX, layoutRotateY);
        });
    }

    @Override
    public void updateLogic() {
        super.updateLogic();
        children.forEach((child)->{child.updateLogic();});
    }

    @Override
    public IDrawableImpl getImplementation() {
        return implementation;
    }

    private void recalculateSize(){
        if(children.size() == 0){
            width = 0;
            height= 0;
        }

        children.forEach((child)->{
            if (child.getWidth() + child.getLayoutX() > width) {
                width = child.getWidth() + child.getLayoutX();
            }
            if (child.getHeight() + child.getLayoutY() > height) {
                height = child.getHeight() + child.getLayoutY();
            }
        });
        updateCollisions();
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public void setX(float newX) {
        super.setX(newX);
        children.forEach((child)->{
            child.setX(newX+child.getLayoutX());
        });
    }

    @Override
    public void setY(float newY) {
        super.setY(newY);
        children.forEach((child)->{
            child.setY(newY+child.getLayoutY());
        });
    }

    @Override
    public void setStaticX(float newStaticX) {
        super.setStaticX(newStaticX);
        children.forEach((child)->{
            child.setX(newStaticX+child.getLayoutX());
        });
    }

    @Override
    public void setStaticY(float newStaticY) {
        super.setStaticY(newStaticY);
        children.forEach((child)->{
            child.setY(newStaticY+child.getLayoutY());
        });
    }

    //COLLISIONS

    @Override
    public boolean collidesWith(Node intruder){
        boolean collides = super.collidesWith(intruder);

        if(collides) return true;

        for(Node child: children){
            if(child.collidesWith(intruder)) return true;
        }

        return false;
    }

    @Override
    public void eventIfCollidesWith(Node intruder){
        super.eventIfCollidesWith(intruder);

        children.forEach((Node child)->child.eventIfCollidesWith(intruder));
    }

    @Override
    public void updateCollisions(){
        super.updateCollisions();

        for(Node child: children){
            child.updateCollisions();
        }
    }
}