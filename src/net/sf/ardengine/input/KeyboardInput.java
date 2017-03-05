package net.sf.ardengine.input;

/**
 * Sent from renderer, contains info about keyboard event.
 */
public class KeyboardInput {
    /**Key pressed/released*/
    public final InputTypes eventType;
    /**Which key*/
    public final Keys keyType;

    /**
     * @param keyType Which key
     * @param eventType Key pressed/released
     */
    public KeyboardInput(Keys keyType, InputTypes eventType) {
        this.keyType = keyType;
        this.eventType = eventType;
    }
}