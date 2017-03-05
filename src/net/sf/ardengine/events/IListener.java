package net.sf.ardengine.events;

/**
 * Listener is used to catch and process Events invoked at Node.
 *
 * Node.registerListener() makes this Listener listen for Node events
 */
public interface IListener {

    /**
     * @return Purpose of event to listen for(faster than instanceof)
     */
    public EventType getType();

    /**
     * User method for dealing with event
     * @param event catched event with data about situation
     */
    public void process(IEvent event);
}
