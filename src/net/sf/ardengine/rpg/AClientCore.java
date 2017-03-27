package net.sf.ardengine.rpg;

import net.sf.ardengine.rpg.multiplayer.INetwork;
import net.sf.ardengine.rpg.multiplayer.INetworkedNode;
import net.sf.ardengine.rpg.multiplayer.JsonMessage;

/**
 * Handles client game logic.
 */
public class AClientCore extends ANetworkCore{

    /**
     * @param network Responsible for communicating with other clients
     */
    protected AClientCore(INetwork network) {
        super(network);

        handlers.put(INetworkedNode.StoredStates.TYPE, (JsonMessage message) -> {
            INetworkedNode node = synchronizedNodes.get(message.getTargetNodeID());
            if(node != null){
                node.receiveState(message);
            }
        });
    }

    @Override
    protected final void updateCoreLogic(int passedFrames) {
        synchronizedNodes.values().forEach((INetworkedNode iNetworkedNode) -> {
            iNetworkedNode.triggerState(passedFrames);
        });
    }

}
