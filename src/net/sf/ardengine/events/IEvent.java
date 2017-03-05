package net.sf.ardengine.events;

/**
 * Interface for events, which contain information about specific situation.
 *
 * Node.invokeEvent() processes this event through list of Node listeners
 */
public interface IEvent {

    /**
     * @return Purpose of event(faster than instanceof)
     */
    public EventType getEventType();

    /**
     * Blocks further propagation of event
     */
    public void consume();

    /**
     * @return True, if event should not be propagated further
     */
    public boolean isConsumed();
}