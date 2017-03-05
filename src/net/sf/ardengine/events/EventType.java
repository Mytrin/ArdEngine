package net.sf.ardengine.events;

/**
 * Enum of used Event types
 */
public enum EventType {
    /**Used on collision detection*/
    COLLISION,
    /**For user*/
    CUSTOM,
    /**Called when Node is removed from game*/
    REMOVAL,

    MOUSE_MOVED,
    MOUSE_OUT,
    MOUSE_PRESSED,
    MOUSE_RELEASED,
    MOUSE_CLICKED,
    MOUSE_DRAG_STARTED,
    MOUSE_DRAGGED,
    MOUSE_DRAG_ENDED,

    KEY_PRESSED,
    KEY_RELEASED
}