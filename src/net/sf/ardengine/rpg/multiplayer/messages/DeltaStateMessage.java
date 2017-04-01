package net.sf.ardengine.rpg.multiplayer.messages;

import net.sf.ardengine.rpg.multiplayer.INetworkedNode;

/**
 * Message containing changed properties of networked object
 * prepared for sending to ClientNetworkCores.
 */
public class DeltaStateMessage extends JsonMessage {

    /**Type of JSON message containing state info*/
    public static final String TYPE = "node-delta-state";
    /**Target state index*/
    public static final String STATE_INDEX = "state-index";

    /**
     * @param targetNode node, about which is state informs
     * @param serverTime current game time
     * @param currentStateIndex state, when changes occurred
     */
    public DeltaStateMessage(INetworkedNode targetNode,
                             long serverTime, int currentStateIndex) {
        super(TYPE, targetNode.getJSONDeltaState(), targetNode);
        json.addProperty(TIMESTAMP, serverTime);
        json.addProperty(STATE_INDEX, currentStateIndex);
    }
}
