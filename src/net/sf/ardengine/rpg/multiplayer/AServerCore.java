package net.sf.ardengine.rpg.multiplayer;

import net.sf.ardengine.rpg.multiplayer.messages.*;
import net.sf.ardengine.rpg.multiplayer.network.INetwork;
import net.sf.ardengine.rpg.multiplayer.network.INetworkPlayer;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles server game logic.
 */
public abstract class AServerCore extends ANetworkCore {

    private long serverTimestamp = 0;

    protected boolean isSinglePlayer = false;

    private final JsonMessageHandler joinHandler = (JsonMessage message) -> {
        allowJoin(message);
    };

    private final JsonMessageHandler initPlayerHandler = (JsonMessage message) -> {
        prepareClient(message.sourceMessage.getSender());
    };

    /**
     * Create server core for single player
     * @param playerName name of player
     */
    protected AServerCore(String playerName) {
        super(new DummyNetwork(playerName));
        isSinglePlayer = true;
    }

    /**
     * @param network Responsible for communicating with other clients
     */
    protected AServerCore(INetwork network) {
        super(network);
        initHandlers();
    }

    private void initHandlers(){
        handlers.put(JoinRequestMessage.TYPE, joinHandler);
        handlers.put(ClientPreparedMessage.TYPE, initPlayerHandler);
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
        if(changedIndex && !isSinglePlayer){

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

    /**
     * Overridable method for user.
     *
     * End with network.sendMessage(request.sourceMessage.getSender(),
      *                 new JoinResponseMessage(true, reason).toString());
     *
     * @param request received message with request for join
     */
    protected void allowJoin(JsonMessage request){
        network.sendMessage(request.sourceMessage.getSender(),
                new JoinResponseMessage(true, null).toString());
    }

    /**
     *  Method for server, where he can exchange with client info
     *
     *  End with network.sendMessage(player,
     *                 new ClientReadyMessage().toString());
     *
     *  @param player Newly joined player
     */
    protected abstract void prepareClient(INetworkPlayer player);



}
