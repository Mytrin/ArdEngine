package net.sf.ardengine.events;

import net.sf.ardengine.Node;
import net.sf.ardengine.collisions.ACollisionShape;

/**
 * Event, which is used by Collision detection
 */
public class CollisionEvent extends AEvent{

    /**Node, which collided*/
    private Node me;

    /**Node, with which receiver collided*/
    private Node intruder;

    /**Node's ACollisionShape, which collided*/
    private ACollisionShape myShape;
    /**Intruder Node's ACollisionShape, which collided*/
    private ACollisionShape intruderShape;

    /**
     *
     * @param me Node, which collided
     * @param intruder Node, with which receiver collided
     * @param myShape Node's ACollisionShape, which collided
     * @param intruderShape Intruder Node's ACollisionShape, which collided
     */
    public CollisionEvent(Node me, Node intruder, ACollisionShape myShape, ACollisionShape intruderShape){
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
     * @return Intruder Node's ACollisionShape, which collided
     */
    public ACollisionShape getIntruderShape() {
        return intruderShape;
    }

    /**
     * @return Source Node's ACollisionShape, which collided
     */
    public ACollisionShape getMyShape() {
        return myShape;
    }

    /**
     * @return this event from intruder' point of view
     */
    public CollisionEvent swap(){
        return new CollisionEvent(intruder, me, intruderShape, myShape);
    }
}
