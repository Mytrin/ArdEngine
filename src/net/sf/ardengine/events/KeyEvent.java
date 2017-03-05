package net.sf.ardengine.events;

import net.sf.ardengine.input.InputTypes;
import net.sf.ardengine.input.KeyboardInput;
import net.sf.ardengine.input.Keys;

/**
 * Event dispatched by InputManager
 */
public class KeyEvent extends AEvent{
    /**Keyboard key, which was pressed/released*/
    private final Keys affectedKey;

    /**
     * @param type Type of key event - PRESSED/RELEASED
     * @param affectedKey Keyboard key, which was pressed/released
     */
    public KeyEvent(EventType type, Keys affectedKey){
        super(type);
        this.affectedKey = affectedKey;
    }

    /**
     * @param input class containing information about event
     */
    public KeyEvent(KeyboardInput input){
        super((input.eventType==InputTypes.PRESSED)?EventType.KEY_PRESSED:EventType.KEY_RELEASED);
        this.affectedKey = input.keyType;
    }

    /**
     * @return Keyboard key, which was pressed/released
     */
    public Keys getAffectedKey() {
        return affectedKey;
    }
}