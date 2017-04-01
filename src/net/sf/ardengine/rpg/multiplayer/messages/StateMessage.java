package net.sf.ardengine.rpg.multiplayer.messages;

import net.sf.ardengine.rpg.multiplayer.INetworkedNode;

/**
 * Message sent by server when loading new level to joined clients
 * to synchronize with changes, which occurred since game started.
 */
public class StateMessage extends JsonMessage {

    /**Type of JSON message containing complete state info*/
    public static final String TYPE = "node-state";

    /**
     * @param targetNode node, about which is state informs
     * @param serverTime current game time
     * @param currentStateIndex state, when changes occurred
     */
    public StateMessage(INetworkedNode targetNode,
                        long serverTime, int currentStateIndex) {
        super(TYPE, targetNode.getJSONState(), targetNode);
    }
}
