package net.sf.ardengine.input;

import net.sf.ardengine.Core;
import net.sf.ardengine.Node;
import net.sf.ardengine.collisions.CollisionPolygon;
import net.sf.ardengine.events.EventType;
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

    /**
     * Checks collision and dispatches required events
     * @param mouseX mouse coordinate at actual scene
     * @param mouseY mouse coordinate at actual scene
     */
    public void update(float mouseX, float mouseY){
        setX(mouseX/ Core.renderer.getWindowWidth()* Core.renderer.getBaseWindowWidth());
        setY(mouseY/ Core.renderer.getWindowHeight()* Core.renderer.getBaseWindowHeight());
        for(Node node: Core.getNodes()){
            if(node.isCollideable()){
                NodeMouseState state = node.getMouseState();
                if(mayIntersectWith(node) && collidesWith(node)){
                    if(!state.isMouseOver){
                        state.isMouseOver = true;
                        //System.out.println("Mouse moved");
                        node.invokeEvent(new MouseEvent(EventType.MOUSE_MOVED, InputTypes.MOUSE_NONE, getX(), getY()));
                    }
                }else{
                    if(state.isMouseOver && !state.isMouseDragged){
                        state.isMouseOver = false;
                        state.mouseOut();
                        //System.out.println("Mouse out");
                        node.invokeEvent(new MouseEvent(EventType.MOUSE_OUT, InputTypes.MOUSE_NONE, getX(), getY()));
                    }
                }
            }
        }

        for(Node node: draggedNodes){
            node.invokeEvent(new MouseEvent(EventType.MOUSE_DRAGGED, InputTypes.MOUSE_NONE, getX(), getY()));
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
        for(Node node: Core.getNodes()) {
            if (node.isCollideable()) {
                NodeMouseState state = node.getMouseState();

                if (mayIntersectWith(node) && collidesWith(node)) {
                    if(!state.isMouseOver){
                        state.isMouseOver = true;
                        //System.out.println("Mouse move");
                        node.invokeEvent(new MouseEvent(EventType.MOUSE_MOVED, button, getX(), getY()));
                    }

                    if(!state.isMousePressed) {
                        if (node.isDraggable() && !state.isMouseDragged) {
                            state.isMouseDragged = true;
                            draggedNodes.add(node);
                            //System.out.println("Mouse drag start");
                            node.invokeEvent(new MouseEvent(EventType.MOUSE_DRAG_STARTED, button, getX(), getY()));
                        }
                    }

                    if(state.pressedMouseButton(button)){
                        //System.out.println("Mouse pressed");
                        node.invokeEvent(new MouseEvent(EventType.MOUSE_PRESSED, button, getX(), getY()));
                    }

                }else{
                    if(state.isMouseOver && !draggedNodes.contains(node)){
                        state.isMouseOver = false;
                        state.mouseOut();
                        //System.out.println("Mouse out");
                        node.invokeEvent(new MouseEvent(EventType.MOUSE_OUT, button, getX(), getY()));
                    }
                }
            }
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

        for(Node node: Core.getNodes()) {
            if (node.isCollideable()) {
                NodeMouseState state = node.getMouseState();

                if (mayIntersectWith(node) && collidesWith(node)) {
                    if(!state.isMouseOver){
                        state.isMouseOver = true;
                        //System.out.println("Mouse moved");
                        node.invokeEvent(new MouseEvent(EventType.MOUSE_MOVED, button, getX(), getY()));
                    }

                    if(state.isMousePressed){
                        state.releasedMouseButton(button);
                        //System.out.println("Mouse clicked");
                        node.invokeEvent(new MouseEvent(EventType.MOUSE_CLICKED, button, getX(), getY()));
                    }
                    //System.out.println("Mouse released");
                    node.invokeEvent(new MouseEvent(EventType.MOUSE_RELEASED, button, getX(), getY()));
                }else{
                    if(state.isMouseOver && !draggedNodes.contains(node)){
                        state.isMouseOver = false;
                        state.mouseOut();
                        //System.out.println("Mouse out");
                        node.invokeEvent(new MouseEvent(EventType.MOUSE_OUT, button, getX(), getY()));
                    }
                }
            }
        }

        for(Node dragged : draggedNodes){
            dragged.getMouseState().isMouseDragged = false;
            dragged.invokeEvent(new MouseEvent(EventType.MOUSE_DRAG_ENDED, button, getX(), getY()));
        }
        draggedNodes.clear();
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