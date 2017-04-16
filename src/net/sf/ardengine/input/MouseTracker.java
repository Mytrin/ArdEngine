package net.sf.ardengine.input;

import net.sf.ardengine.Core;
import net.sf.ardengine.Group;
import net.sf.ardengine.Node;
import net.sf.ardengine.collisions.CollisionPolygon;
import net.sf.ardengine.events.EventType;
import net.sf.ardengine.events.IEvent;
import net.sf.ardengine.events.MouseEvent;
import net.sf.ardengine.renderer.IDrawableImpl;

import java.util.LinkedList;
import java.util.List;

/**
 * This is helper class for InputManager for
 * testing mouse collisions. Though it extends Node for
 * collision compatibility, it should never be inserted into game drawables!
 *
 * HERE BE EVIL UNDEAD DRAGONS SELLING WASHING MACHINES AND 3D PRINTERS
 * Seriously, just imagine what kind of events are processed here and go doing something less suicidal.
 */
public class MouseTracker extends Node{

    private List<Node> draggedNodes = new LinkedList<>();

    /**Width and height of collision rectangle starting at mouse [0;0]*/
    private int tolerance;

    /**Events are generated every test loop, so they can be consumed*/
    private MouseEvent dragStartEvent = null;
    private MouseEvent dragEndEvent = null;
    private MouseEvent draggedEvent = null;
    private MouseEvent movedEvent = null;
    private MouseEvent exitedEvent = null;
    private MouseEvent pressedEvent = null;
    private MouseEvent releasedEvent = null;
    private MouseEvent clickedEvent = null;

    /**
     * @param tolerance Width and height of collision rectangle starting at mouse [0;0]
     */
   public MouseTracker(int tolerance){
       super();
       this.tolerance = tolerance;
       setCollideable(true);
       updateCollisionRect();
   }

    /**
     * Changes width and height of mouse collision rect
     */
    private void updateCollisionRect(){
        getCollisions().clear();
        getCollisions().add(new CollisionPolygon(new float[]{0, 0, 0, tolerance,
                tolerance, tolerance, tolerance, 0}, this));
    }

    private void regenerateEvents(InputTypes mouseButton){
        float x = getX();
        float y = getY();

        dragStartEvent = new MouseEvent(EventType.MOUSE_DRAG_STARTED, mouseButton, x, y);
        draggedEvent = new MouseEvent(EventType.MOUSE_DRAGGED, mouseButton, x, y);
        dragEndEvent = new MouseEvent(EventType.MOUSE_DRAG_ENDED, mouseButton, x, y);
        movedEvent = new MouseEvent(EventType.MOUSE_MOVED, mouseButton, getX(), getY());
        exitedEvent = new MouseEvent(EventType.MOUSE_OUT, mouseButton, getX(), getY());
        pressedEvent = new MouseEvent(EventType.MOUSE_PRESSED, mouseButton, getX(), getY());;
        releasedEvent = new MouseEvent(EventType.MOUSE_RELEASED, mouseButton, getX(), getY());;
        clickedEvent =  new MouseEvent(EventType.MOUSE_CLICKED, mouseButton, x, y);;
    }

    /**
     * Checks collision and dispatches required events
     * @param mouseX mouse coordinate at actual scene
     * @param mouseY mouse coordinate at actual scene
     */
    public void update(float mouseX, float mouseY){
        setX(mouseX/Core.renderer.getWindowWidth()*Core.renderer.getBaseWindowWidth());
        setY(mouseY/Core.renderer.getWindowHeight()*Core.renderer.getBaseWindowHeight());

        regenerateEvents(InputTypes.MOUSE_NONE);

        for(Node node: Core.getNodes()){
            if(node.isStatic()){
                updateMouseMovementState(node);
            }
        }

        for(Node node: draggedNodes){
            if(node.isStatic()){
                node.invokeEvent(draggedEvent);
            }
        }

        setX(getX() + Core.cameraX);
        setY(getY() + Core.cameraY);

        regenerateEvents(InputTypes.MOUSE_NONE);

        for(Node node: Core.getNodes()){
            if(!node.isStatic()){
                updateMouseMovementState(node);
            }
        }

        for(Node node: draggedNodes){
            if(!node.isStatic()){
                node.invokeEvent(draggedEvent);
            }
        }
    }

    private void updateMouseMovementState(Node node){
        if(node.isCollideable()){

            groupMouseMovement(node);

            NodeMouseState state = node.getMouseState();
            if(mayIntersectWith(node) && collidesWith(node)){
                if(!state.isMouseOver){
                    state.isMouseOver = true;
                    //System.out.println("Mouse moved");
                    node.invokeEvent(movedEvent);
                }
            }else{
                if(state.isMouseOver && !state.isMouseDragged){
                    state.isMouseOver = false;
                    state.mouseOut();
                    //System.out.println("Mouse out");
                    node.invokeEvent(exitedEvent);
                }
            }
        }
    }

    private void groupMouseMovement(Node node){
        if(node instanceof Group){
            ((Group)node).forEachChildren((Node child)->updateMouseMovementState(child));
        }
    }

    /**
     * Checks collision and dispatches required events for pressed mouse
     * @param mouseX mouse coordinate at actual scene
     * @param mouseY mouse coordinate at actual scene
     * @param button related mouse button
     */
    public void mousePressed(InputTypes button, float mouseX, float mouseY){
        setX(mouseX/ Core.renderer.getWindowWidth()* Core.renderer.getBaseWindowWidth());
        setY(mouseY/ Core.renderer.getWindowHeight()* Core.renderer.getBaseWindowHeight());

        regenerateEvents(button);

        for(Node node: Core.getNodes()) {
            if(node.isStatic()) {
                updateMousePressedState(node, button);
            }
        }

        setX(getX() + Core.cameraX);
        setY(getY() + Core.cameraY);

        regenerateEvents(InputTypes.MOUSE_NONE);

        for(Node node: Core.getNodes()){
            if(!node.isStatic()){
                updateMousePressedState(node, button);
            }
        }
    }

    private void updateMousePressedState(Node node, InputTypes button){
        if (node.isCollideable()) {
            NodeMouseState state = node.getMouseState();

            groupMousePressed(node, button);

            if (mayIntersectWith(node) && collidesWith(node)) {
                if(!state.isMouseOver){
                    state.isMouseOver = true;
                    //System.out.println("Mouse move");
                    node.invokeEvent(movedEvent);
                }

                if(!state.isMousePressed) {
                    if (node.isDraggable() && !state.isMouseDragged) {
                        state.isMouseDragged = true;
                        draggedNodes.add(node);
                        //System.out.println("Mouse drag start");
                        node.invokeEvent(dragStartEvent);
                    }
                }

                if(state.pressedMouseButton(button)){
                    //System.out.println("Mouse pressed");
                    node.invokeEvent(pressedEvent);
                }

            }else{
                if(state.isMouseOver && !draggedNodes.contains(node)){
                    state.isMouseOver = false;
                    state.mouseOut();
                    //System.out.println("Mouse out");
                    node.invokeEvent(exitedEvent);
                }
            }
        }
    }

    private void groupMousePressed(Node node, InputTypes button){
        if(node instanceof Group){
            ((Group)node).forEachChildren((Node child)->updateMousePressedState(child, button));
        }
    }

    /**
     * Checks collision and dispatches required events for released mouse
     * @param mouseX mouse coordinate at actual scene
     * @param mouseY mouse coordinate at actual scene
     * @param button related mouse button
     */
    public void mouseReleased(InputTypes button, float mouseX, float mouseY) {
        setX(mouseX / Core.renderer.getWindowWidth() * Core.renderer.getBaseWindowWidth());
        setY(mouseY / Core.renderer.getWindowHeight() * Core.renderer.getBaseWindowHeight());

        regenerateEvents(button);

        for(Node node: Core.getNodes()) {
            if(node.isStatic()) {
                updateMouseReleasedState(node, button);
            }
        }

        for(Node dragged : draggedNodes){
            if(dragged.isStatic()) {
                dragged.getMouseState().isMouseDragged = false;
                dragged.invokeEvent(dragEndEvent);
            }
        }

        setX(getX() + Core.cameraX);
        setY(getY() + Core.cameraY);

        regenerateEvents(InputTypes.MOUSE_NONE);

        for(Node node: Core.getNodes()) {
            if(!node.isStatic()) {
                updateMouseReleasedState(node, button);
            }
        }

        for(Node dragged : draggedNodes){
            if(!dragged.isStatic()) {
                dragged.getMouseState().isMouseDragged = false;
                dragged.invokeEvent(dragEndEvent);
            }
        }

        draggedNodes.clear();
    }

    private void updateMouseReleasedState(Node node, InputTypes button){
        if (node.isCollideable()) {

            groupMouseReleased(node, button);

            NodeMouseState state = node.getMouseState();

            if (mayIntersectWith(node) && collidesWith(node)) {
                if(!state.isMouseOver){
                    state.isMouseOver = true;
                    //System.out.println("Mouse moved");
                    node.invokeEvent(movedEvent);
                }

                if(state.isMousePressed){
                    state.releasedMouseButton(button);
                    //System.out.println("Mouse clicked");
                    node.invokeEvent(clickedEvent);
                }
                //System.out.println("Mouse released");
                node.invokeEvent(releasedEvent);
            }else{
                if(state.isMouseOver && !draggedNodes.contains(node)){
                    state.isMouseOver = false;
                    state.mouseOut();
                    //System.out.println("Mouse out");
                    node.invokeEvent(exitedEvent);
                }
            }
        }
    }

    private void groupMouseReleased(Node node, InputTypes button){
        if(node instanceof Group){
            ((Group)node).forEachChildren((Node child)->updateMouseReleasedState(child, button));
        }
    }

    /**
     * @param tolerance Width and height of collision rectangle starting at mouse [0;0]
     */
    public void setTolerance(int tolerance) {
        this.tolerance = tolerance;
        updateCollisionRect();
        updateCollisions();
    }

    @Override
    public float getWidth() {
        return tolerance;
    }

    @Override
    public float getHeight() {
        return tolerance;
    }

    @Override
    public void updateCollisions() {
        super.updateCollisions();
    }

    @Override
    public IDrawableImpl getImplementation() {
        return null;
    }

    @Override
    public void draw() {
        //VIRTUAL NODE
    }
}