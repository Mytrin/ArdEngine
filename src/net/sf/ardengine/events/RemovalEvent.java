package net.sf.ardengine.events;

/**
 * Used when Node is removed from game
 */
public class RemovalEvent extends  AEvent{

    public RemovalEvent(){
        super(EventType.REMOVAL);
    }

}
