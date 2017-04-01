package net.sf.ardengine.rpg.multiplayer.udplib;

import com.gmail.lepeska.martin.udplib.StoredMessage;
import net.sf.ardengine.rpg.multiplayer.network.INetworkMessage;
import net.sf.ardengine.rpg.multiplayer.network.INetworkPlayer;

/**
 * Layer between ArdEngine GroupUser and UDPLib NetworkPlayer implementation.
 */
public class UDPLibNetworkMessage implements INetworkMessage {

    private final StoredMessage messageImpl;

    /**
     * @param messageImpl original mesage implementation
     */
    public UDPLibNetworkMessage(StoredMessage messageImpl) {
        this.messageImpl = messageImpl;
    }

    @Override
    public String getMessage() {
        return messageImpl.message;
    }

    @Override
    public INetworkPlayer getSender() {
        return new UDPLibNetworkPlayer(messageImpl.sender);
    }

    @Override
    public boolean isPrivate() {
        return !messageImpl.isMulticast;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UDPLibNetworkMessage){
            return ((UDPLibNetworkMessage)obj).messageImpl.equals(messageImpl);
        }
        return false;
    }
}
