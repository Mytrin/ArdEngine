package net.sf.ardengine.rpg.multiplayer.messages;

/**
 * Sent by client after he exchanged all required data to continue joining Game.
 */
public class ClientPreparedMessage extends JsonMessage{

    /**Type of message sent by client after he exchanged all required data to continue joining Game.*/
    public static final String TYPE = "player-join-prepared";

    public ClientPreparedMessage(){
        super(TYPE, null, null);
    }

}
