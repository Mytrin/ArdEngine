package net.sf.ardengine.rpg;

import net.sf.ardengine.rpg.multiplayer.udplib.INetworkedNode;

import java.util.HashMap;

/**
 * Handles game logic.
 */
public abstract class ANetworkCore {

    /**Nodes, about which NetworkCore shares information with other NetworkCores*/
    private HashMap<String, INetworkedNode> synchronizedNodes = new HashMap<>();

    /**
     * Updates Server/Client logic
     * like movement synchronization or pause commands
     * @param delta Time since last update
     */
    public abstract void updateState(long delta);

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

}
