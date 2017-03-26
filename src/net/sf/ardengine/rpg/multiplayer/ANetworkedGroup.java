package net.sf.ardengine.rpg.multiplayer;

import net.sf.ardengine.Group;

/**
 * ArdEngine Group Node with prepared methods for multiplayer implementation
 */
public abstract class ANetworkedGroup extends Group implements INetworkedNode{

    /**Unique identification of this Node in Game and Worlds*/
    protected final String networkID;
    protected final StoredStates storedStates;

    /**
     * @param networkID Unique identification of this Node in Game and Worlds
     */
    public ANetworkedGroup(String networkID) {
        this.networkID = networkID;
        this.storedStates = new StoredStates(this);
    }

    @Override
    public String getID() {
        return networkID;
    }

    @Override
    public StoredStates getStoredStates() {
        return storedStates;
    }
}
