package net.sf.ardengine.rpg.multiplayer.messages;

/**
 * Send automatically by client to synchronize with game.
 */
public class JoinRequestMessage extends JsonMessage{

    /**Type of message sent automatically by client to synchronize with game.*/
    public static final String TYPE = "player-join-request";

    public JoinRequestMessage(){
        super(TYPE, null, null);
    }

}
