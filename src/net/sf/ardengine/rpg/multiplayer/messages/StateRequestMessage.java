package net.sf.ardengine.rpg.multiplayer.messages;

/**
 * Sent automatically by client to synchronize with game.
 */
public class StateRequestMessage extends JsonMessage{

    /**Type of message sent automatically by client to synchronize with game.*/
    public static final String STATE_REQUEST_TYPE = "player-state-request";

    public StateRequestMessage(){
        super(STATE_REQUEST_TYPE, null, null);
    }

}
