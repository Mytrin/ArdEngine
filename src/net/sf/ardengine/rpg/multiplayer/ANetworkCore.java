package net.sf.ardengine.rpg.multiplayer;

import com.google.gson.JsonParser;
import net.sf.ardengine.Core;
import net.sf.ardengine.rpg.multiplayer.messages.JsonMessage;
import net.sf.ardengine.rpg.multiplayer.messages.JsonMessageHandler;
import net.sf.ardengine.rpg.multiplayer.network.INetwork;
import net.sf.ardengine.rpg.multiplayer.network.INetworkMessage;

import java.util.HashMap;
import java.util.logging.Logger;

/**
 * Handles game logic.
 */
public abstract class ANetworkCore {
    /**State update frequency (How many game frames it takes to advance to next state)*/
    public static final int FRAMES_PER_STATE = 3;

    /**Index of state in NetworkedNode's buffer*/
    protected int actualIndex = 0;
    /**progress to new state*/
    protected int actualFrame = 0;
    /**true, if actualIndex has been changed this update*/
    protected boolean changedIndex = false;

    /**True, if Core is currently processing*/
    protected boolean isStarted = true;

    /**Nodes, about which NetworkCore shares information with other NetworkCores*/
    protected HashMap<String, INetworkedNode> synchronizedNodes = new HashMap<>();

    /**Responsible for communicating with other clients*/
    protected final INetwork network;

    protected HashMap<String, JsonMessageHandler> handlers = new HashMap<>();
    protected JsonParser parser = new JsonParser();

    /**
     * @param network Responsible for communicating with other clients
     */
    protected ANetworkCore(INetwork network) {
        this.network = network;
    }

    /**
     * Processes received messages and starts updateCoreLogic()
     * @param delta Time since last update
     */
    public final void updateState(long delta){
        int passedFrames = countPassedFrames(delta);

        if(isStarted){
            updateStateIndex(passedFrames);
            processReceivedMessages();
            updateCoreLogic(delta, passedFrames);
        }
    }

    /**
     * Updates Server/Client logic
     * @param delta time passed since last update
     * @param passedFrames Frames passed since last update
     */
    protected abstract void updateCoreLogic(long delta, int passedFrames);

    private void processReceivedMessages(){
        network.getMessages().forEach((INetworkMessage iNetworkMessage) -> {
            JsonMessage receivedMessage = new JsonMessage(parser, iNetworkMessage);
            handle(receivedMessage);
        });
    }

    private void handle(JsonMessage message){
        JsonMessageHandler handler = handlers.get(message.getType());
        if(handler != null){
            handler.handle(message);
        }else{
            Logger.getLogger(ANetworkCore.class.getName()).warning(
                    "Unspecified type of JSONMessage: "+message.getType()+". Message ignored.");
        }
    }

    private void updateStateIndex(int passedFrames){
        actualFrame+=passedFrames;

        if(actualFrame<FRAMES_PER_STATE-1){
            changedIndex = false;
            return;
        }

        while(actualFrame>FRAMES_PER_STATE-1){
            actualFrame-=FRAMES_PER_STATE;
            actualIndex++;
            changedIndex = true;

            if(actualIndex==INetworkedNode.StoredStates.STATES_BUFFER_SIZE){
                actualIndex=0;
            }
        }
    }

    /**
     * @return Frames passed since last renderer update
     */
    private int countPassedFrames(long delta){
        float frameLength = 1f/(float)Core.renderer.getDesiredFPS();
        return (int)(delta/frameLength);
    }

    /**
     * Inserts node into list of nodes, which state is shared with server/clients.
     * @param node added Node with unique ID
     */
    public void addNode(INetworkedNode node){
        synchronizedNodes.put(node.getID(), node);
    }

    /**
     * Removes node from list of nodes, which state is shared with server/clients.
     * @param node removed Node with unique ID
     */
    public void removeNode(INetworkedNode node){
        synchronizedNodes.remove(node.getID());
    }

    /**
     * Starts Core INetwork update/notify process
     */
    public void start(){
        isStarted = true;
    }

}
