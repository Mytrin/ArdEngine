package net.sf.ardengine;

import javafx.scene.paint.Color;
import net.sf.ardengine.collisions.CollisionShape;
import net.sf.ardengine.collisions.PrivateAreaRect;
import net.sf.ardengine.events.CollisionEvent;
import net.sf.ardengine.events.EventType;
import net.sf.ardengine.events.IEvent;
import net.sf.ardengine.events.IListener;
import net.sf.ardengine.input.NodeMouseState;

import java.util.LinkedList;
import java.util.List;

/**
 * Basic game unit - handles interaction logic,
 * but rendering is left to children.
 */
public abstract class Node implements IDrawable{

	//REAL location, used for rendering and collisions
	protected float x = 0;
	protected float y = 0;
	protected float z = 0;

    //LOCKED location, so it looks like that drawable is stucked on one place whenever camera moves
    protected float staticX = 0;
    protected float staticY = 0;
    protected boolean isStaticLocked = false;

    //GROUP OFFSET location
    protected float layoutX = 0;
    protected float layoutY = 0;
    protected float layoutOffsetX = 0;
    protected float layoutOffsetY = 0;

   /**Opacity of this Node, if it's possible */
   protected float opacity=1.0f;
   /**Scale of this Node, if it's possible*/
   protected float scale=1.0f;
   /**Rotation of this Node, if it's possible */
   protected float angle=0f;
	/**Color or fill, if it's possible*/
	protected Color color = Color.BLACK;

    /**Listeners checking events at this Node*/
	protected List<IListener> listeners = new LinkedList<>();

    /**True, if node should be tested for collisions*/
    private boolean isCollideable = false;
    /**True, if node should receive special events about mouse dragging*/
    private boolean isDraggable = false;
    /**Object with info about mouse state */
    private NodeMouseState mouseState = new NodeMouseState();
    /**List of collisions for this node*/
    protected LinkedList<CollisionShape> collisions = new LinkedList<>();
    /**Decides, if collision is possible*/
    protected PrivateAreaRect privateArea;


	protected List<DelayedAction> delayedActions = new LinkedList<>();
    protected boolean hasDelayedActions = false;

    /** Called every game loop;
     * method for "AI" for simple single player games;
     * Do not forget for super.updateLogic(), when implementing!
     */
    public void updateLogic(){
        if(hasDelayedActions){
            DelayedAction[] actions = new DelayedAction[delayedActions.size()];
            actions = delayedActions.toArray(actions);

            delayedActions.clear();
            hasDelayedActions = false;

            for(DelayedAction action : actions){
                action.action();
            }
        }
    }

	@Override
	public float getX() {
		if(!isStaticLocked) return x+layoutOffsetX;
		return staticX;
	}

	@Override
	public void setX(float newX) {
		x = newX;
        updateCollisions();
	}
	
	@Override
	public float getY() {
        if(!isStaticLocked) return y+layoutOffsetY;
        return staticY;
	}

	@Override
	public void setY(float newY) {
		y = newY;
        updateCollisions();
	}
	
	@Override
	public float getZ() {
		return z;
	}

	@Override
	public void setZ(float newZ) {
		z = newZ;
		Core.childrenZChanged(this);
	}
	
	@Override
	public float getStaticX() {
		return staticX;
	}
	
	@Override
	public void setStaticX(float newStaticX) {
		staticX = newStaticX;
        updateCollisions();
	}

	@Override
	public float getStaticY() {
		return staticY;
	}
	
	@Override
	public void setStaticY(float newStaticY) {
		staticY = newStaticY;
        updateCollisions();
	}

	@Override
	public boolean isStatic() {
		return isStaticLocked;
	}


	@Override
	public void setStatic(boolean staticLock) {
		isStaticLocked = staticLock;
	}

    @Override
    public void setLayoutX(float layoutX) {
        this.layoutX = layoutX;
    }

    @Override
    public float getLayoutX() {
        return layoutX;
    }

    @Override
    public void setLayoutY(float layoutY) {
        this.layoutY = layoutY;
    }

    @Override
    public float getLayoutY() {
        return layoutY;
    }

    @Override
    public void setLayoutOffsetCoords(float newLayoutOffsetX, float newLayoutOffsetY) {
        this.layoutOffsetX = newLayoutOffsetX;
        this.layoutOffsetY = newLayoutOffsetY;
    }

    @Override
    public float getLayoutOffsetX() {
        return layoutOffsetX;
    }

    @Override
    public float getLayoutOffsetY() {
        return layoutOffsetY;
    }

    @Override
   public void setOpacity(float newOpacity){
        opacity=newOpacity;
        getImplementation().update();
   }
   
	@Override
   public float getOpacity(){
       return opacity;
   }

	@Override
   public void setScale(float newScale){
       scale=newScale;
        getImplementation().update();
        updateCollisions();
     }

	@Override
   public float getScale(){
       return scale;
   }

	@Override
   public void setAngle(float angle) {
	    this.angle = angle;
        getImplementation().update();
        updateCollisions();
   }

	@Override
   public float getAngle(){
       return angle;
   }

	@Override
	public void setColor(javafx.scene.paint.Color color) {
		this.color = color;
        getImplementation().update();
	}

	@Override
	public Color getColor() {
		return color;
	}

    //EVENTS

    /**
     * Makes given Listener listen for thia Node's events
     * @param newListener object responsible for processing event
     */
    public void registerListener(IListener newListener){
        listeners.add(newListener);
    }

    /**
     * Removes listener from list
     * @param oldListener object responsible for processing event
     */
    public void removeListener(IListener oldListener){
        listeners.remove(oldListener);
    }

    /**
     * Processes this event through list of Node listeners
     * @param event object with data about some situation to deal with
     */
    public void invokeEvent(IEvent event){
        if(event.isConsumed()){
            return;
        }

        EventType type = event.getEventType();

        for(IListener listener : listeners){
            if(listener.getType() == type){
                listener.process(event);
                if(event.isConsumed()){
                    return;
                }
            }
        }
    }

    //COLLISIONS

    /**
     * @return True, if node should be tested for collisions
     */
    public boolean isCollideable() {
        return isCollideable;
    }

    /**
     *
     * @param collideable True, if node should be tested for collisions
     */
    public void setCollideable(boolean collideable) {
       if(collideable){
           if(getWidth() < 1 || getHeight() < 1){
               delayAction(()->{
                   setCollideable(true);
               });
           }else{
               privateArea = new PrivateAreaRect(this);
               isCollideable = true;
           }
       }else{
           isCollideable = false;
           privateArea = null;
       }

    }

    /**
     * @return List of collisions for this node
     */
    public LinkedList<CollisionShape> getCollisions() {
        return collisions;
    }

    /**
     * @param intruder Node with whom may this node intersect
     * @return True, if privateAreas collide
     */
    public boolean mayIntersectWith(Node intruder){
        if(!isCollideable || !intruder.isCollideable) return false;
        return privateArea.intersects(intruder.privateArea);
    }


    /**
     * Checks all collideable objects in world and game and returns those which's privateArea is intersecting with this.
     * -
     * @return Nodes which's privateArea is intersecting with this as ArrayList
     */
    public  LinkedList<Node> checkAreaToList(){
        LinkedList<Node> intruders= new LinkedList<>();

        if(!isCollideable) return intruders;

        for(Node node : Core.getNodes()){
            if(node == this) continue;

            if (node.isCollideable()) {
                if (mayIntersectWith(node)) {
                    intruders.add(node);
                }
            }
        }

        return intruders;
    }

    /**
     * @param intruder Node, which was detected by checkAreaToList() or mayIntersectWith()
     * @return true, if nodes really collide
     */
    public boolean collidesWith(Node intruder){
        if(!isCollideable) return false;

        for(CollisionShape collision : collisions){
            for(CollisionShape intruderCollision : intruder.collisions){
                if(collision.isColliding(intruderCollision)){
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * @param intruder Node, which was detected by checkAreaToList() or mayIntersectWith()
     * @return CollisionEvent, if nodes really collide, or null
     */
    public CollisionEvent eventIfCollidesWith(Node intruder){
        if(!isCollideable) return null;

        for(CollisionShape collision : collisions){
            for(CollisionShape intruderCollision : intruder.collisions){
                if(collision.isColliding(intruderCollision)){
                    return new CollisionEvent(this, intruder, collision, intruderCollision);
                }
            }
        }
        return null;
    }

    /**
     * Updates all node's collision shapes to keep actual coords
     */
    public void updateCollisions(){
        if(!isCollideable) return;

        privateArea.updateProperties();
        for(CollisionShape cs : collisions){
            cs.updateProperties(this);
        }
    }
    /**
     * @return object responsible for detecting close Nodes
     */
    public PrivateAreaRect getPrivateArea() {
        return privateArea;
    }

    /**
     * @return True, if node should receive special events about mouse dragging
     */
    public boolean isDraggable() {
        return isDraggable;
    }

    /**
     * @param draggable Should receive special events about mouse dragging?
     */
    public void setDraggable(boolean draggable) {
        isDraggable = draggable;
    }

    /**
     * @return Object with info about mouse state
     */
    public NodeMouseState getMouseState() {
        return mouseState;
    }

    protected void delayAction(DelayedAction action){
        delayedActions.add(action);
        hasDelayedActions = true;
    }

    /**
     * Sometimes is not possible to do requested action immediately
     * (getting text width while font is not loaded yet).
     * For such cases is possible to delay actions to future and
     * invoke them next Frame by automatic updateLogic().
     */
    protected interface DelayedAction{
        /**
         * action for next frame
         */
        public void action();
    }
}