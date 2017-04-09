package net.sf.ardengine.rpg.multiplayer.messages;

/**
 * Sent by server to notify client, that he can start the Level.
 */
public class ClientReadyMessage extends JsonMessage{

    /**Type of message sent by server to notify client, that he can start the Level.*/
    public static final String TYPE = "player-join-prepared";

    public ClientReadyMessage(){
        super(TYPE, null, null);
    }

}
