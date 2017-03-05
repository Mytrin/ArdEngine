package net.sf.ardengine.rpg;

import net.sf.ardengine.Core;
import net.sf.ardengine.IGame;

/**
 * Game class with set up RPG features.
 */
public abstract class RPGame implements IGame {

    /**Game logic handler*/
   private ANetworkCore logicalCore;

    //TODO dialogs


    @Override
    public void gameRun() {
        if(logicalCore != null){
            logicalCore.updateState(Core.renderer.getDelta());
        }
    }

    public ANetworkCore getLogicalCore() {
        return logicalCore;
    }
}
