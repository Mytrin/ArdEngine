package net.sf.ardengine.input;

/**
 * Sent from renderer, contains info about mouse event.
 */
public class MouseInput {
    /**Button pressed/released*/
    final InputTypes eventType;
    /**Which button*/
    final InputTypes buttonType;
    /**X coord of mouse at the time of event*/
    final float mouseX;
    /**Y coord of mouse at the time of event*/
    final float mouseY;

    /**
     * @param eventType Button pressed/released
     * @param buttonType Which button
     * @param mouseX X coord of mouse at the time of event
     * @param mouseY Y coord of mouse at the time of event
     */
    public MouseInput(InputTypes eventType, InputTypes buttonType, float mouseX, float mouseY) {
        this.eventType = eventType;
        this.buttonType = buttonType;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }
}