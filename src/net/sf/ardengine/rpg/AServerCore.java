package net.sf.ardengine.rpg;

import net.sf.ardengine.rpg.multiplayer.INetwork;
import net.sf.ardengine.rpg.multiplayer.INetworkedNode;
import net.sf.ardengine.rpg.multiplayer.JsonMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles server game logic.
 */
public abstract class AServerCore extends ANetworkCore{

    private long serverTimestamp = 0;

    /**
     * @param network Responsible for communicating with other clients
     */
    protected AServerCore(INetwork network) {
        super(network);
    }

    @Override
    protected final void updateCoreLogic(long delta, int passedFrames) {
        serverTimestamp += delta;

        synchronizedNodes.values().forEach((INetworkedNode iNetworkedNode) ->
            iNetworkedNode.updateServerState()
        );

        handleServerLogic(passedFrames);

        sendNodeStates();
    }

    /**
     * Overridable method for user
     *
     * @param passedFrames frames passed since last update
     */
    public abstract void handleServerLogic(int passedFrames);

    /**
     * Obtains current JSON state from networked nodes and sends it to client
     */
    private void sendNodeStates(){
        if(changedIndex){

            List<JsonMessage> messages = new LinkedList<>();

            synchronizedNodes.values().forEach((INetworkedNode iNetworkedNode) -> {
                if(iNetworkedNode.hasChangedState()){
                    messages.add(iNetworkedNode.getJsonMessage(actualIndex, serverTimestamp));
                }
            });

            messages.forEach((JsonMessage message)->
                    network.sendBroadcastMessage(message.toString())
            );
        }
    }

}
