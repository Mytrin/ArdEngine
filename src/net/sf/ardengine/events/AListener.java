package net.sf.ardengine.events;

public abstract class AListener implements IListener {

    private final EventType eventType;

    public AListener(EventType eventType) {
        this.eventType = eventType;
    }

    @Override
    public EventType getType() {
        return eventType;
    }
}
