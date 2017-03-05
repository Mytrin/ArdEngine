package net.sf.ardengine.input;

/**
 * Classes implementing this interface can be used by
 * InputManager.setActiveKeyTyper() to be notified about typed keys
 * from keyboard.
 */
public interface IKeyTyper {

    /**
     * Called from InputManager, if this object
     * was set as active by InputManager.setActiveKeyTyper()
     * @param key typed character
     */
    public void keyTyped(char key);

    /**
     * Called from InputManager, if this object
     * was set as active by InputManager.setActiveKeyTyper()
     */
    public void backspacePressed();

    /**
     * Called from InputManager, if this object
     * was set as active by InputManager.setActiveKeyTyper()
     */
    public void enterPressed();
}