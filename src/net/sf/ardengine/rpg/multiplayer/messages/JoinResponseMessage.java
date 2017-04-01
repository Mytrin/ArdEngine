package net.sf.ardengine.rpg.multiplayer.messages;

/**
 * Sent automatically by server as reaction on JoinRequestMessage
 */
public class JoinResponseMessage extends JsonMessage{

    /**Type of message sent automatically by server as reaction on JoinRequestMessage*/
    public static final String TYPE = "player-join-response";

    private static final String JOIN_RESPONSE_ATTR = "response";
    private static final String JOIN_RESPONSE_OK = "OK";

    /**
     * @param accept true, if server allows client to join
     * @param reason optional, reason why client is not permitted to join
     */
    public JoinResponseMessage(boolean accept, String reason){
        super(TYPE, null, null);
        json.addProperty(JOIN_RESPONSE_ATTR, accept?JOIN_RESPONSE_OK:reason);
    }

    /**
     * @param message message sent automatically by server as reaction on JoinRequestMessage
     * @return true, if server permits client to join game
     */
    public static boolean isOK(JsonMessage message){
        return message.getValueAsString(JOIN_RESPONSE_ATTR).equals(JOIN_RESPONSE_OK);
    }

    /**
     *
     * @param message message sent automatically by server as reaction on JoinRequestMessage
     * @return reason why server refused to let client join game
     */
    public static String getReason(JsonMessage message){
        return message.getValueAsString(JOIN_RESPONSE_ATTR);
    }

}
