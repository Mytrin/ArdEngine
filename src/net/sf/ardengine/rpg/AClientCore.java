package net.sf.ardengine.rpg;

import net.sf.ardengine.rpg.multiplayer.INetwork;
import net.sf.ardengine.rpg.multiplayer.INetworkedNode;
import net.sf.ardengine.rpg.multiplayer.JsonMessage;

/**
 * Handles client game logic.
 */
public class AClientCore extends ANetworkCore{

    /**How many states is client delayed after server*/
    public static final int CLIENT_LAGGED_STATES = 2;

    private static final int CLIENT_LAG = 100;
    private static final long CURRENT_TIME_INDEFINITE = -1;
    private long updateTimeStamp = CURRENT_TIME_INDEFINITE;

    /**
     * @param network Responsible for communicating with other clients
     */
    protected AClientCore(INetwork network) {
        super(network);

        handlers.put(INetworkedNode.StoredStates.TYPE, (JsonMessage message) -> {
            INetworkedNode node = synchronizedNodes.get(message.getTargetNodeID());
            if(node != null){
                updateStateIfNotLate(node, message);
            }
        });
    }

    @Override
    protected final void updateCoreLogic(long delta, int passedFrames) {
        if(updateTimeStamp != CURRENT_TIME_INDEFINITE) updateTimeStamp+= delta;

        synchronizedNodes.values().forEach((INetworkedNode iNetworkedNode) -> {
            iNetworkedNode.triggerState(actualIndex, actualFrame);
        });
    }

    private void updateStateIfNotLate(INetworkedNode node, JsonMessage message){
        long messageTimestamp = message.getValueAsLong(JsonMessage.TIMESTAMP);
        int messageIndex = message.getValueAsInt(JsonMessage.STATE_INDEX);

        if(updateTimeStamp == CURRENT_TIME_INDEFINITE){ //initialize timestamp on start
            updateTimeStamp = messageTimestamp - CLIENT_LAG;

            initIndex(messageIndex);
        }

        // If datagram arrived too late, we have to drop it
        if(messageTimestamp >= updateTimeStamp){
            node.receiveState(message);
        }
    }

    private void initIndex(int serverIndex){
        actualIndex = serverIndex - 2;
        if(actualIndex < 0){
            actualIndex  = INetworkedNode.StoredStates.STATES_BUFFER_SIZE - serverIndex;
        }
    }

}
