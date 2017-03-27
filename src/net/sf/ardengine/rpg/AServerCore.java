package net.sf.ardengine.rpg;

import net.sf.ardengine.rpg.multiplayer.INetwork;
import net.sf.ardengine.rpg.multiplayer.INetworkedNode;

/**
 * Handles server game logic.
 */
public abstract class AServerCore extends ANetworkCore{

    /**
     * @param network Responsible for communicating with other clients
     */
    protected AServerCore(INetwork network) {
        super(network);
    }

//TODO sending states(can be null), current index

    @Override
    protected final void updateCoreLogic(int passedFrames) {
        synchronizedNodes.values().forEach((INetworkedNode iNetworkedNode) -> {
            iNetworkedNode.updateServerState();
        });

        handleServerLogic(passedFrames);
    }

    /**
     * Overridable method for user
     *
     * @param passedFrames frames passed since last update
     */
    public abstract void handleServerLogic(int passedFrames);

}
