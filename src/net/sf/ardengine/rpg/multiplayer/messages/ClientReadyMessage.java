package net.sf.ardengine.rpg.multiplayer.messages;

/**
 * Sent by server to notify client, that he can start the Level.
 */
public class ClientReadyMessage extends JsonMessage{

    /**Type of message sent by server to notify client, that he can start the Level.*/
    public static final String CLIENT_PREPARED_TYPE = "player-join-prepared";

    public ClientReadyMessage(){
        super(CLIENT_PREPARED_TYPE, null, null);
    }

}
