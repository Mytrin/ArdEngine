package net.sf.ardengine.events;

import net.sf.ardengine.Node;
import net.sf.ardengine.collisions.CollisionShape;

/**
 * Event, which is used by Collision detection
 */
public class CollisionEvent extends AEvent{

    /**Node, which collided*/
    private Node me;

    /**Node, with which receiver collided*/
    private Node intruder;

    /**Node's CollisionShape, which collided*/
    private CollisionShape myShape;
    /**Intruder Node's CollisionShape, which collided*/
    private CollisionShape intruderShape;

    /**
     *
     * @param me Node, which collided
     * @param intruder Node, with which receiver collided
     * @param myShape Node's CollisionShape, which collided
     * @param intruderShape Intruder Node's CollisionShape, which collided
     */
    public CollisionEvent(Node me, Node intruder, CollisionShape myShape, CollisionShape intruderShape){
        super(EventType.COLLISION);
        this.me = me;
        this.intruder = intruder;
        this.myShape = myShape;
        this.intruderShape = intruderShape;
    }

    /**
     * @return Node, with which receiver collided
     */
    public Node getIntruder() {
        return intruder;
    }

    /**
     * @return Intruder Node's CollisionShape, which collided
     */
    public CollisionShape getIntruderShape() {
        return intruderShape;
    }

    /**
     * @return Source Node's CollisionShape, which collided
     */
    public CollisionShape getMyShape() {
        return myShape;
    }

    /**
     * @return this event from intruder' point of view
     */
    public CollisionEvent swap(){
        return new CollisionEvent(intruder, me, intruderShape, myShape);
    }
}
