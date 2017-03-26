package net.sf.ardengine.rpg;

import net.sf.ardengine.Core;
import net.sf.ardengine.rpg.multiplayer.INetwork;
import net.sf.ardengine.rpg.multiplayer.INetworkedNode;

import java.util.HashMap;

/**
 * Handles game logic.
 */
abstract class ANetworkCore {
    /**State update frequency*/
    public static final int FRAMES_PER_STATE = 3;

    private int actualIndex = 0;
    private int actualFrame = 0;

    /**True, if Core is currently processing*/
    protected boolean isStarted = false;

    /**Nodes, about which NetworkCore shares information with other NetworkCores*/
    private HashMap<String, INetworkedNode> synchronizedNodes = new HashMap<>();

    /**Responsible for communicating with other clients*/
    protected final INetwork network;



    /**
     * @param network Responsible for communicating with other clients
     */
    protected ANetworkCore(INetwork network) {
        this.network = network;
    }

    /**
     * Updates Server/Client logic
     * like movement synchronization or pause commands
     * @param delta Time since last update
     */
    public void updateState(long delta){
        updateStateIndex(countPassedFrames(delta));
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

    private void updateStateIndex(int passedFrames){
        actualFrame+=passedFrames;

        while(actualFrame>FRAMES_PER_STATE-1){
            actualFrame-=FRAMES_PER_STATE;
            actualIndex++;

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
}
