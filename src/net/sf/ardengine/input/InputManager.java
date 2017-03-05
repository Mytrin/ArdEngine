package net.sf.ardengine.input;

import net.sf.ardengine.Core;
import net.sf.ardengine.events.EventType;
import net.sf.ardengine.events.IEvent;
import net.sf.ardengine.events.IListener;
import net.sf.ardengine.events.KeyEvent;

import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

/**
 * Singleton maintaining information about mouse and keyboard events.
 */

public class InputManager {
    /**Inputs from mouse received over last frame*/
    private List<MouseInput> mouseInputs = new LinkedList<>();
    /**Inputs from keyboard received over last frame*/
    private List<KeyboardInput> keyboardInputs = new LinkedList<>();
    /**Typed characters*/
    private List<CharInput> charInputs = new LinkedList<>();
    /**Array with state of known keyboard keys, true==pressed*/
    private boolean[] pressedKeys = new boolean[256];
    /**For testing mouse collision*/
    private MouseTracker mouse = new MouseTracker(4);
    /**Active object listening for typed keys*/
    private IKeyTyper activeKeyTyper = null;
    /**Listener for notifying activeKeyTyper about backspace*/
    private IListener activeInputListener = new IListener() {
        @Override
        public EventType getType() {
            return EventType.KEY_PRESSED;
        }

        @Override
        public void process(IEvent event) {
            if(activeKeyTyper == null) return;

            if(((KeyEvent)event).getAffectedKey() == Keys.KEY_BACKSPACE){
                activeKeyTyper.backspacePressed();
            }else if(((KeyEvent)event).getAffectedKey() == Keys.KEY_ENTER){
                activeKeyTyper.enterPressed();
            }
        }
    };
    /**Listeners checking key events*/
    private List<IListener> listeners = new LinkedList<>();

    /**Singleton*/
    private static final InputManager instance = new InputManager();

    /**Singleton*/
    private InputManager(){
        listeners.add(activeInputListener);
    };

    /**
     * @return object maintaining information about mouse and keyboard events.
     */
    public static InputManager getInstance() {
        return instance;
    }

    /**Processes info about mouse and keyboard from renderer
     * and creates events.
     */
    public void update(){
        mouseInputs.forEach((report)->{
            if(report.eventType==InputTypes.PRESSED){
                mouse.mousePressed(report.buttonType, report.mouseX, report.mouseY);
            }else{
                mouse.mouseReleased(report.buttonType, report.mouseX, report.mouseY);
            }
        });
        mouseInputs.clear();

        mouse.update(Core.renderer.getMouseX(), Core.renderer.getMouseY());

        keyboardInputs.forEach((report)->{
            if(report.eventType == InputTypes.PRESSED){
                pressedKeys[Keys.getKeyIndex(report.keyType)] = true;
            }else{
                pressedKeys[Keys.getKeyIndex(report.keyType)] = false;
            }

            KeyEvent keyEvent = new KeyEvent(report);
            listeners.forEach((listener)->{
                if(keyEvent.getEventType() == listener.getType()){
                    listener.process(keyEvent);
                }
            });
        });
        keyboardInputs.clear();

        charInputs.forEach((report)->{
            if(activeKeyTyper != null){
                activeKeyTyper.keyTyped(report.input);
            }
        });
        charInputs.clear();
    }

    /**
     * Adds given input to queue for next update()
     * @param input info about event
     */
    public void insertMouseInput(MouseInput input){
        mouseInputs.add(input);
    }

    /**
     * Adds given input to queue for next update()
     * @param input info about event
     */
    public void insertKeyboardInput(KeyboardInput input){
        keyboardInputs.add(input);
    }

    /**
     * Adds given input to queue for next update()
     * @param input info about event
     */
    public void insertCharInput(CharInput input){
        charInputs.add(input);
    }

    /**
     * @param key key, which may be pressed
     * @return true, if key with given enum name is pressed
     */
    public boolean isKeyPressed(Keys key){
        int index = Keys.getKeyIndex(key);
        return pressedKeys[index];
    }

    /**
     * @param activeKeyTyper  object to be notified about typed keys
     */
    public void setActiveKeyTyper(IKeyTyper activeKeyTyper) {
        this.activeKeyTyper = activeKeyTyper;
    }

    /**
     * @return actual object to be notified about typed keys or null
     */
    public IKeyTyper getActiveKeyTyper() {
        return activeKeyTyper;
    }

    /**
     * Makes given Listener listen for global key press and release events
     * @param newListener object responsible for processing event
     */
    public void registerListener(IListener newListener){
        listeners.add(newListener);
    }

    /**
     * Removes listener from list
     * @param oldListener object responsible for processing event
     */
    public void removeListener(IListener oldListener){
        listeners.remove(oldListener);
    }

    /**
     * Changes look of mouse cursor
     * @param is - input stream to source image
     */
    public void setMouseImage(InputStream is){
        Core.renderer.setMouseImage(is);
    }

    /**
     * Changes look of mouse cursor
     * @param url - path to source image
     */
    public void setMouseImage(String url){
        Core.renderer.setMouseImage(url);
    }

    /**
     * @param tolerance distance from mouse [0;0], which is still accepted as collision
     */
    public void setMouseTolerance(int tolerance){
        mouse.setTolerance(tolerance);
    }

    /**
     * @return mouse X coord at base window size
     */
    public float getMouseX(){
        return mouse.getX();
    }

    /**
     * @return mouse Y coord at base window size
     */
    public float getMouseY(){
        return mouse.getY();
    }
}