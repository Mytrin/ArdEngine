package net.sf.ardengine.rpg.multiplayer;

import com.google.gson.JsonObject;
import net.sf.ardengine.rpg.multiplayer.messages.*;
import net.sf.ardengine.rpg.multiplayer.network.INetwork;

/**
 * Handles client game logic.
 */
public abstract class AClientCore extends ANetworkCore {

    /**How many states is client delayed after server*/
    public static final int CLIENT_LAGGED_STATES = 2;

    private static final int CLIENT_LAG = 100;
    private static final long CURRENT_TIME_INDEFINITE = -1;
    private long updateTimeStamp = CURRENT_TIME_INDEFINITE;

    private final JsonMessageHandler joinHandler = (JsonMessage message) -> {
        handlers.remove(JoinResponseMessage.TYPE);

        if(JoinResponseMessage.isOK(message)){
            prepareClient();
        }else{
            throw new RuntimeException(JoinResponseMessage.getReason(message));
        }
    };

    private final JsonMessageHandler joinFinished = (JsonMessage message) -> {

    };

    private final JsonMessageHandler deltaStateHandler = (JsonMessage message) -> {
        INetworkedNode node = synchronizedNodes.get(message.getTargetNodeID());
        if(node != null){
            updateStateIfNotLate(node, message);
        }
    };

    private final JsonMessageHandler stateHandler = (JsonMessage message) -> {
        INetworkedNode node = reconstructFromState(message.getContent());
        if(!synchronizedNodes.containsKey(node.getID())){
            addNode(node);
        }
    };

    /**
     * @param network Responsible for communicating with other clients
     */
    protected AClientCore(INetwork network) {
        super(network);

        handlers.put(DeltaStateMessage.TYPE, deltaStateHandler);

        join();
    }

    private void join(){
        network.sendBroadcastMessage(new JoinRequestMessage().toString());
        handlers.put(JoinResponseMessage.TYPE, joinHandler);
        handlers.put(StateMessage.TYPE, stateHandler);
        handlers.put(ClientReadyMessage.TYPE ,joinFinished);
    }

    /**
     *  Method for client, where he can exchange with server additional info
     *  before obtaining game data from server.
     *
     *  METHOD HAS TO END WITH:JoinRequestMessage
     *  network.sendBroadcastMessage(new ClientPreparedMessage().toString());
     */
    protected abstract void prepareClient();

    @Override
    protected final void updateCoreLogic(long delta, int passedFrames) {
        if(updateTimeStamp != CURRENT_TIME_INDEFINITE) updateTimeStamp+= delta;

        synchronizedNodes.values().forEach((INetworkedNode iNetworkedNode) -> {
            iNetworkedNode.triggerState(actualIndex, actualFrame);
        });
    }

    private void updateStateIfNotLate(INetworkedNode node, JsonMessage message){
        long messageTimestamp = message.getValueAsLong(JsonMessage.TIMESTAMP);
        int messageIndex = message.getValueAsInt(DeltaStateMessage.STATE_INDEX);

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

    /**
     * Called automatically by client when joining game.
     * Method for user to create/synchronize nodes and add them to Game.
     * @param state Complete state of node returned by INetworkedNode.getJSONState()
     * @return reconstructed networked node
     */
    protected abstract INetworkedNode reconstructFromState(JsonObject state);

}
