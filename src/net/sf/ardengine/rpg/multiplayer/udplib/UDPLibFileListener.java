package net.sf.ardengine.rpg.multiplayer.udplib;

import com.gmail.lepeska.martin.udplib.files.IFileShareListener;
import net.sf.ardengine.rpg.multiplayer.INetworkFileListener;

import java.io.File;

/**
 * Layer between ArdEngine INetworkFileListener and UDPLib IFileShareListener implementation.
 */
public class UDPLibFileListener implements IFileShareListener {

    protected final INetworkFileListener gameListener;

    /**
     * @param gameListener ArdEngine INetworkListener to be mapped as IGroupListener
     */
    public UDPLibFileListener(INetworkFileListener gameListener) {
        this.gameListener = gameListener;
    }

    @Override
    public void onFinished(File file) {
        gameListener.onFinished(file);
    }

    @Override
    public void onFail(File file, Exception e) {
        gameListener.onFail(file, e);
    }
}
