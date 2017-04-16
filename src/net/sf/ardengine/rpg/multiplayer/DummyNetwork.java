package net.sf.ardengine.rpg.multiplayer;

import net.sf.ardengine.rpg.multiplayer.network.*;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

/**
 * INetwork implementation for single player games
 */
class DummyNetwork implements INetwork {

    private final String username;

    /**
     * @param username player name
     */
    DummyNetwork(String username) {
        this.username = username;
    }

    @Override
    public void start() {}

    @Override
    public void leave() {}

    @Override
    public List<INetworkMessage> getMessages() {
        return new LinkedList<>();
    }

    @Override
    public List<INetworkPlayer> getCurrentUsers() {
        return new LinkedList<>();
    }

    @Override
    public INetworkPlayer getServerUser() {
        return null;
    }

    @Override
    public void sendMessage(INetworkPlayer target, String message) {}

    @Override
    public void sendBroadcastMessage(String message) {}

    @Override
    public void addListener(INetworkListener listener) {}

    @Override
    public void removeListener(INetworkListener listener) {}

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public void shareFile(File file, String fileName, INetworkFileListener listener) {
        listener.onFinished(file);
    }
}
