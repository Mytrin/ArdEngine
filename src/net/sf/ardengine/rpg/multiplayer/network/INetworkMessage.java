package net.sf.ardengine.rpg.multiplayer.network;

/**
 * Defines which information about message from other users should be available for NetworkLogicalCore
 */
public interface INetworkMessage {
    /**
     * @return Message Content
     */
    public String getMessage();

    /**
     * @return Sender
     */
    public INetworkPlayer getSender();

    /**
     * @return true, if message is meant only for receiver
     */
    public boolean isPrivate();
}
