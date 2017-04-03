package net.sf.ardengine.rpg.multiplayer.udplib;

import com.gmail.lepeska.martin.udplib.IGroupListener;
import com.gmail.lepeska.martin.udplib.client.GroupUser;
import net.sf.ardengine.rpg.multiplayer.network.INetworkListener;

import java.io.File;

/**
 * Layer between ArdEngine NetworkListener and UDPLib IGroupListener implementation.
 */
public class UDPLibNetworkListener implements IGroupListener{

    protected final INetworkListener gameListener;

    /**
     * @param gameListener ArdEngine NetworkListener to be mapped as IGroupListener
     */
    public UDPLibNetworkListener(INetworkListener gameListener) {
        this.gameListener = gameListener;
    }

    @Override
    public void joined(GroupUser groupUser) {
        gameListener.joined(new UDPLibNetworkPlayer(groupUser));
    }

    @Override
    public void userKicked(GroupUser groupUser) {
        gameListener.userKicked(new UDPLibNetworkPlayer(groupUser));
    }

    @Override
    public void userJoined(GroupUser groupUser) {
        gameListener.userJoined(new UDPLibNetworkPlayer(groupUser));
    }

    @Override
    public void mesageReceived() {
        gameListener.mesageReceived();
    }

    @Override
    public void kicked() {
        gameListener.kicked();
    }

    @Override
    public void fileReceived(String fileID, File file) {
        gameListener.fileReceived(fileID, file);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof UDPLibNetworkListener){
            return obj.equals(this);
        }
        return false;
    }
}
