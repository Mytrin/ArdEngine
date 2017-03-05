package net.sf.ardengine.input;

/**
 * Simple data class for storing info about mouse/node state.
 */
public class NodeMouseState {
    /**True, if mouse is currently hovering over node*/
    boolean isMouseOver = false;
    /**True, if mouse is currently left pressed node*/
    boolean isMouseLeftPressed = false;
    /**True, if mouse is currently right pressed node*/
    boolean isMouseRightPressed = false;
    /**True, if mouse is currently mid pressed node*/
    boolean isMouseMidPressed = false;
    /**True, if any of buttons is pressed on this node*/
    boolean isMousePressed = false;
    /**True, if mouse is currently dragging node*/
    boolean isMouseDragged = false;

    /**
     * @param button pressed mouse button
     * @return true, if state has changed
     */
    boolean pressedMouseButton(InputTypes button){
        isMousePressed = true;

        if (button == InputTypes.MOUSE_LEFT && !isMouseLeftPressed) {
            isMouseLeftPressed = true;
            return true;
        } else if (button == InputTypes.MOUSE_RIGHT && !isMouseRightPressed) {
            isMouseRightPressed = true;
            return true;
        } else if (button == InputTypes.MOUSE_RIGHT && !isMouseMidPressed) {
            isMouseMidPressed = true;
            return true;
        }

        return false;
    }

    /**
     * @param button released mouse button
     * @return true, if state has changed
     */
    boolean releasedMouseButton(InputTypes button){
        if (button == InputTypes.MOUSE_LEFT && isMouseLeftPressed) {
            isMouseLeftPressed = false;
            isMousePressed = isMouseLeftPressed || isMouseRightPressed || isMouseMidPressed;
            return true;
        } else if (button == InputTypes.MOUSE_RIGHT && isMouseRightPressed) {
            isMouseRightPressed = false;
            isMousePressed = isMouseLeftPressed || isMouseRightPressed || isMouseMidPressed;
            return true;
        } else if (button == InputTypes.MOUSE_RIGHT && isMouseMidPressed) {
            isMouseMidPressed = false;
            isMousePressed = isMouseLeftPressed || isMouseRightPressed || isMouseMidPressed;
            return true;
        }

        return false;
    }

    /**
     * Sets all * pressed to false
     */
    void mouseOut(){
        isMouseLeftPressed = false;
        isMouseRightPressed = false;
        isMouseMidPressed = false;
        isMousePressed = false;
    }
}