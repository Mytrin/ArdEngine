package net.sf.ardengine.events;

import net.sf.ardengine.input.InputTypes;

/**
 * Event dispatched by InputManager
 */
public class MouseEvent extends AEvent{

    /**X Coord of mouse at the time of event*/
    private final float mouseX;
    /**Y Coord of mouse at the time of event*/
    private final float mouseY;
    /**Left/Right/Middle/None*/
    private final InputTypes button;
    /**
     * @param type Type of mouse event
     * @param button Left/Right/Middle/None
     * @param mouseX X Coord of mouse at the time of event
     * @param mouseY Y Coord of mouse at the time of event
     */
    public MouseEvent(EventType type, InputTypes button, float mouseX, float mouseY){
        super(type);
        this.button = button;
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    /**
     * @return X Coord of mouse at the time of event
     */
    public float getMouseX() {
        return mouseX;
    }

    /**
     * @return Y Coord of mouse at the time of event
     */
    public float getMouseY() {
        return mouseY;
    }
}