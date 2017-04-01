package net.sf.ardengine.rpg.multiplayer.network;

import java.io.File;

/**
 * Simple listener for distributor side of file sharing
 */
public interface INetworkFileListener {

    /**
     * Called, when file has been successfully sent to network
     * @param file shared file
     */
    public void onFinished(File file);

    /**
     * Called, when file cannot be shared for some reason
     * @param file shared file
     * @param e reason, while sharing failed
     */
    public void onFail(File file, Exception e);

}
