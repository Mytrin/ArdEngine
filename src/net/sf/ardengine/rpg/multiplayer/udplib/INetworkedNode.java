package net.sf.ardengine.rpg.multiplayer.udplib;

/**
 * Interface for Nodes synchronized by ANetworkCore.
 */
public interface INetworkedNode {

    /**
     * @return Unique identification of this Node in Game and Worlds
     */
    public String getID();

    /**Handles received state updates from Server*/
    public void updateClientState();

    /**Handles standard game logic, which would be at Node.updateLogic() for simple games*/
    public void updateServerState();

    /**
     * @return Changed properties of this object prepared for sending to ClientNetworkCores
     */
    public String createJSONState();
}
