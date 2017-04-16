package net.sf.ardengine.rpg.multiplayer.network;

import java.io.File;
import java.util.List;

/**
 * Defines which methods should be available for NetworkLogicalCore
 */
public interface INetwork {

    /**
     * Starts connecting or creation of network depending on implementing class
     */
    public void start();

    /**
     * Leaves current network
     */
    public void leave();

    /**
     * @return received messages since last call of getMessages()
     */
    public List<INetworkMessage> getMessages();

    /**
     * @return latest info about network users
     */
    public List<INetworkPlayer> getCurrentUsers();

    /**
     * @return object representing user acting as server
     */
    public INetworkPlayer getServerUser();

    /**
     * Sends message to other client in Network
     * @param target recipient
     * @param message data
     */
    public void sendMessage(INetworkPlayer target, String message);

    /**
     * Sends message to other clients in Network
     * @param message data
     */
    public void sendBroadcastMessage(String message);

    /**
     * Adds given listener to current listeners
     * @param listener User's class responsible for dealing with events
     */
    public void addListener(INetworkListener listener);

    /**
     * Removes given listener from current listeners
     * @param listener User's class responsible for dealing with events
     */
    public void removeListener(INetworkListener listener);

    /**
     * @return User's name in network
     */
    public String getUserName();

    /**
     * Sends file to current members of Network
     * @param file file to send
     * @param fileName id of file for other clients
     * @param listener object to notify, when task is finished
     */
    public void shareFile(File file, String fileName, INetworkFileListener listener);
}
