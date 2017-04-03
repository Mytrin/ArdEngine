package net.sf.ardengine.rpg.multiplayer.network;

import java.io.File;

/**
 * Defines which listener-like methods should be available for NetworkLogicalCore
 */
public interface INetworkListener {

    /**
     * Called on successful join(or creation) to Network
     * @param me how am I represented to other members of network
     */
    public void joined(INetworkPlayer me);

    /**
     * Called when client is notified about loss of INetworkPlayer
     * @param who lost group user
     */
    public void userKicked(INetworkPlayer who);

    /**
     * Called when client detects new INetworkPlayer
     * @param who new group user
     */
    public void userJoined(INetworkPlayer who);

    /**
     * Called when thread received new message
     */
    public void mesageReceived();

    /**
     * Called when client had to leave network
     */
    public void kicked();

    /**
     * Called when file has been shared from network
     * @param fileID name of file
     * @param file received file
     */
    public void fileReceived(String fileID, File file);

}
