package net.sf.ardengine.renderer.opengl;

import java.util.logging.Logger;
import net.sf.ardengine.input.*;
import org.lwjgl.glfw.*;

import static org.lwjgl.glfw.GLFW.*;

public class CallbackInput {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	private final GLFWErrorCallback errorCallback = GLFWErrorCallback.create((error, description)
            -> LOGGER.severe(error+": "+GLFWErrorCallback.getDescription(description)));
	
	private GLFWKeyCallback winKeyCallback;
    private GLFWCharModsCallback charInputCallback;
	private GLFWCursorPosCallback  winCurPosCallback;
    private GLFWMouseButtonCallback  winCurBtnCallback;

    /**Coord of mouse on Scene, counts with actual window Size!*/
	private float mouseX = 0;
    /**Coord of mouse on Scene, counts with actual window Size!*/
	private float mouseY = 0;

	public CallbackInput() {
        glfwSetErrorCallback(errorCallback);

	}
	
	public void init(long windowHandle){
		winCurPosCallback = new GLFWCursorPosCallback() {
			
			@Override
			public void invoke(long window, double xpos, double ypos) {
				mouseX = (float)xpos;
				mouseY = (float)ypos;
			}
		};
		glfwSetCursorPosCallback(windowHandle, winCurPosCallback);

        winCurBtnCallback = new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {

                if(action == GLFW_REPEAT) return;

                InputTypes buttonType;
                InputTypes eventType = (action == GLFW_PRESS)?InputTypes.PRESSED:InputTypes.RELEASED;

                switch(button){
                    case GLFW_MOUSE_BUTTON_LEFT: buttonType = InputTypes.MOUSE_LEFT;
                                                    break;
                    case GLFW_MOUSE_BUTTON_RIGHT: buttonType = InputTypes.MOUSE_RIGHT;
                                                    break;
                    default: buttonType = InputTypes.MOUSE_MIDDLE;
                                                    break;
                }

                InputManager.getInstance().insertMouseInput(new MouseInput(eventType, buttonType, mouseX, mouseY));
            }
        };
        glfwSetMouseButtonCallback(windowHandle, winCurBtnCallback);

		winKeyCallback = new GLFWKeyCallback() {

			@Override
			public void invoke(long window, int key, int scancode, int action, int mods) {
                if((action == GLFW_REPEAT)) return;

                InputTypes eventType = (action == GLFW_PRESS)?InputTypes.PRESSED:InputTypes.RELEASED;
                Keys keyType = GLFWToArdKeys.getInstance().getArdKey(key);

                InputManager.getInstance().insertKeyboardInput(new KeyboardInput(keyType, eventType));
            }
		};
		glfwSetKeyCallback(windowHandle, winKeyCallback);

        charInputCallback = new GLFWCharModsCallback() {
            @Override
            public void invoke(long window, int codepoint, int mods) {
                InputManager.getInstance().insertCharInput(new CharInput((char)codepoint));
            }
        };
        glfwSetCharModsCallback(windowHandle, charInputCallback);
	}

    /**
     * @return REAL mouse coord in window
     */
    public float getMouseY() {
        return mouseY;
    }

    /**
     * @return REAL mouse coord in window
     */
    public float getMouseX() {
        return mouseX;
    }
}