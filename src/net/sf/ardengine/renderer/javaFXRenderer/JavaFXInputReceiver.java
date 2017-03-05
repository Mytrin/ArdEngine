package net.sf.ardengine.renderer.javaFXRenderer;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import net.sf.ardengine.input.*;

/**
 * Class for managing JavaFX event listeners and forwarding them to InputManager.
 */
public class JavaFXInputReceiver {

    /**Coord of mouse on Scene, counts with actual window Size!*/
    private float mouseX = 0;
    /**Coord of mouse on Scene, counts with actual window Size!*/
    private float mouseY = 0;

    /**
     * Attaches input listeners to given Scene
     * @param scene object, to which will be listeners attached
     */
    public JavaFXInputReceiver(Scene scene){
        scene.setOnMouseMoved(event -> {
            mouseX = (float)event.getX();
            mouseY = (float)event.getY();
        });

        scene.setOnMousePressed(event ->{
            InputTypes buttonType;

            switch(event.getButton()){
                case PRIMARY: buttonType = InputTypes.MOUSE_LEFT;
                    break;
                case SECONDARY: buttonType = InputTypes.MOUSE_RIGHT;
                    break;
                default: buttonType = InputTypes.MOUSE_MIDDLE;
                    break;
            }
            InputManager.getInstance().insertMouseInput(new MouseInput(InputTypes.PRESSED, buttonType, (float)event.getX(), (float)event.getY()));
        });

        scene.setOnMouseReleased(event ->{
            InputTypes buttonType;

            switch(event.getButton()){
                case PRIMARY: buttonType = InputTypes.MOUSE_LEFT;
                    break;
                case SECONDARY: buttonType = InputTypes.MOUSE_RIGHT;
                    break;
                default: buttonType = InputTypes.MOUSE_MIDDLE;
                    break;
            }
            InputManager.getInstance().insertMouseInput(new MouseInput(InputTypes.RELEASED, buttonType, (float)event.getX(), (float)event.getY()));
        });

        scene.setOnKeyTyped(event -> {
            //This is ugly workaround for JavaFx typing backspace
            //KeyCode.isLetter() does not work on linux czech keyboard
            String typedChar = event.getCharacter();
            if(typedChar.trim().length() == 0 && !typedChar.equals(" ")) return;

            InputManager.getInstance().insertCharInput(new CharInput(typedChar.charAt(0)));
        });

        scene.setOnKeyPressed(event ->{
            Keys keyType = FXToArdKeys.getInstance().getArdKey(event.getCode());

            InputManager.getInstance().insertKeyboardInput(new KeyboardInput(keyType, InputTypes.PRESSED));
        });

        scene.setOnKeyReleased(event ->{
            Keys keyType = FXToArdKeys.getInstance().getArdKey(event.getCode());

            InputManager.getInstance().insertKeyboardInput(new KeyboardInput(keyType, InputTypes.RELEASED));
        });
    }

    /**
     * @return REAL mouse coord in window
     */
    public float getMouseX() {
        return mouseX;
    }

    /**
     * @return REAL mouse coord in window
     */
    public float getMouseY() {
        return mouseY;
    }
}