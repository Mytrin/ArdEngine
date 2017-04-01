package net.sf.ardengine.rpg.multiplayer.network;

import java.net.InetAddress;

/**
 * Defines which information about other users should be available for NetworkLogicalCore
 */
public interface INetworkPlayer {

    /**
     * @return Name of user in network
     */
    public String getName();

    /**
     * @return IP of user in network
     */
    public InetAddress getIP();

    /**
     * @return Time, which it takes to packet from this user to reach Host
     */
    public long getPingToHost();
}
