package net.sf.ardengine.rpg.multiplayer;

import net.sf.ardengine.ASprite;
import net.sf.ardengine.Entity;

/**
 * ArdEngine Entity Node with prepared methods for multiplayer implementation
 */
public abstract class ANetworkedEntity extends Entity implements INetworkedNode{

    /**Unique identification of this Node in Game and Worlds*/
    protected final String networkID;
    protected final StoredStates storedStates;

    public ANetworkedEntity(ASprite sprite, float x, float y, String networkID) {
        super(sprite, x, y);
        this.networkID = networkID;
        this.storedStates = new StoredStates(this);
    }

    public ANetworkedEntity(ASprite sprite, float x, float y, float layoutX, float layoutY, String networkID) {
        super(sprite, x, y, layoutX, layoutY);
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
