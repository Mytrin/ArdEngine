package net.sf.ardengine.renderer.javaFXRenderer;

import javafx.scene.input.KeyCode;
import net.sf.ardengine.input.Keys;

import java.util.HashMap;

import static javafx.scene.input.KeyCode.*;
import static net.sf.ardengine.input.Keys.*;

/**
 * Converter form FX keycode to ArdEngine key enum value
 */
public class FXToArdKeys {
    /**Accepted keys*/
    private static Keys[] ardKeys = new Keys[]{
    KEY_UNKNOWN, KEY_SPACE, KEY_COMMA, KEY_MINUS, KEY_PERIOD, KEY_SLASH, KEY_0, KEY_1, KEY_2,
            KEY_3, KEY_4, KEY_5, KEY_6, KEY_7, KEY_8, KEY_9, KEY_SEMICOLON, KEY_EQUAL, KEY_A, KEY_B, KEY_C,
            KEY_D, KEY_E, KEY_F, KEY_G, KEY_H, KEY_I, KEY_J, KEY_K, KEY_L, KEY_M, KEY_N, KEY_O, KEY_P, KEY_Q,
            KEY_R, KEY_S, KEY_T, KEY_U, KEY_V, KEY_W, KEY_X, KEY_Y, KEY_Z, KEY_LEFT_BRACKET, KEY_BACKSLASH,
            KEY_RIGHT_BRACKET, KEY_GRAVE_ACCENT, KEY_ESCAPE, KEY_ENTER, KEY_TAB,
            KEY_BACKSPACE, KEY_INSERT, KEY_DELETE, KEY_RIGHT, KEY_LEFT, KEY_DOWN, KEY_UP, KEY_PAGE_UP,
            KEY_PAGE_DOWN, KEY_HOME, KEY_END, KEY_CAPS_LOCK, KEY_SCROLL_LOCK, KEY_NUM_LOCK, KEY_PRINT_SCREEN,
            KEY_PAUSE, KEY_F1, KEY_F2, KEY_F3, KEY_F4, KEY_F5, KEY_F6, KEY_F7, KEY_F8, KEY_F9, KEY_F10, KEY_F11,
            KEY_F12, KEY_F13, KEY_F14, KEY_F15, KEY_F16, KEY_F17, KEY_F18, KEY_F19, KEY_F20, KEY_F21, KEY_F22, KEY_F23,
            KEY_F24, KEY_DECIMAL, KEY_DIVIDE, KEY_MULTIPLY, KEY_SUBTRACT, KEY_ADD,
            KEY_SHIFT, KEY_CONTROL, KEY_LEFT_ALT, KEY_SUPER,
            KEY_RIGHT_ALT, KEY_MENU};
    /**Accepted FX key keycodes*/
    private static KeyCode[] fxKeys = new KeyCode[]{UNDEFINED, SPACE,
            COMMA, MINUS, PERIOD, SLASH, NUMPAD0,
            NUMPAD1, NUMPAD2, NUMPAD3, NUMPAD4, NUMPAD5, NUMPAD6, NUMPAD7, NUMPAD8,
            NUMPAD9, SEMICOLON, EQUALS, A, B, C, D,
            E, F, G, H, I, J, K, L,
            M, N, O, P, Q, R, S, T,
            U, V, W, X, Y, Z, OPEN_BRACKET,
            BACK_SLASH, CLOSE_BRACKET, DEAD_GRAVE,
            ESCAPE, ENTER, TAB, BACK_SPACE, INSERT, DELETE,
            RIGHT, LEFT, DOWN, UP, PAGE_UP, PAGE_DOWN,
            HOME, END, CAPS, SCROLL_LOCK, NUM_LOCK,
            PRINTSCREEN, PAUSE, F1, F2, F3, F4,
            F5, F6, F7, F8, F9, F10, F11,
            F12, F13, F14, F15, F16, F17, F18,
            F19, F20, F21, F22, F23, F24,
            DECIMAL, DIVIDE, MULTIPLY,
            SUBTRACT, ADD, SHIFT,
            CONTROL, ALT, WINDOWS,
            ALT_GRAPH, CONTEXT_MENU};

    /**Singleton*/
    private static FXToArdKeys instance  = new FXToArdKeys();
    /**Map for FX -> Ard converting*/
    private HashMap<KeyCode, Keys> table;

    private FXToArdKeys(){
        table = new HashMap<>();
        for(int i=0; i < ardKeys.length; i++){
            table.put(fxKeys[i], ardKeys[i]);
        }
    }

    /**
     * @param keycode pressed/released key
     * @return Ard enum value for given scancode
     */
    public Keys getArdKey(KeyCode keycode){
        Keys toReturn = table.get(keycode);

        if(toReturn==null) toReturn = KEY_UNKNOWN;

        return toReturn;
    }

    /**Returns converter form FX scancode to ArdEngine ones*/
    public static FXToArdKeys getInstance() {
        return instance;
    }
}