package net.sf.ardengine.events;

/**
 * Basic IEvent functionalities implementation
 */
public abstract class AEvent implements IEvent{

    /**Purpose of Event*/
    private final EventType type;

    /**True if event should not be propagated further*/
    private boolean isConsumed = false;

    /**
     * Basic IEvent functionalities implementation
     * @param type purpose of Event
     */
    public AEvent(EventType type){
        this.type = type;
    }

    @Override
    public EventType getEventType() {
        return type;
    }

    /**
     * @return Custom event type, in case getEventType() == EventType.CUSTOM
     */
    public String getCustomEventType() {
        return null;
    }

    @Override
    public void consume() {
        isConsumed = true;
    }

    @Override
    public boolean isConsumed() {
        return isConsumed;
    }
}