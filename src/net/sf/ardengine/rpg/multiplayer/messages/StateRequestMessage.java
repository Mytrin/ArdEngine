package net.sf.ardengine.rpg.multiplayer.messages;

/**
 * Sent automatically by client to synchronize with game.
 */
public class StateRequestMessage extends JsonMessage{

    /**Type of message sent automatically by client to synchronize with game.*/
    public static final String TYPE = "player-state-request";

    public StateRequestMessage(){
        super(TYPE, null, null);
    }

}
