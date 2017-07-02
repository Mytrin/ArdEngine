package net.sf.ardengine.rpg.multiplayer.udplib;

import com.gmail.lepeska.martin.udplib.client.GroupUser;
import net.sf.ardengine.rpg.multiplayer.network.INetworkPlayer;

import java.net.InetAddress;

/**
 * Layer between ArdEngine GroupUser and UDPLib NetworkPlayer implementation.
 */
class UDPLibNetworkPlayer implements INetworkPlayer {

    private final GroupUser playerImpl;

    /**
     * @param playerImpl Implementation version of network user
     */
    UDPLibNetworkPlayer(GroupUser playerImpl) {
        this.playerImpl = playerImpl;
    }

    @Override
    public InetAddress getIP() {
        return playerImpl.ip;
    }

    @Override
    public String getName() {
        return playerImpl.name;
    }

    @Override
    public long getPingToHost() {
        return playerImpl.getPingToHost();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UDPLibNetworkPlayer){
            return ((UDPLibNetworkPlayer)obj).playerImpl.equals(playerImpl);
        }
        return false;
    }
}
