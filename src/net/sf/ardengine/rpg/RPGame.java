package net.sf.ardengine.rpg;

import net.sf.ardengine.Core;
import net.sf.ardengine.IGame;
import net.sf.ardengine.dialogs.Dialogs;
import net.sf.ardengine.rpg.multiplayer.ANetworkCore;

/**
 * Game class with set up RPG features.
 */
public abstract class RPGame implements IGame {

    /**Game logic handler*/
    protected ANetworkCore logicalCore;
    /**Dialog loader*/
    public final Dialogs dialogs = new Dialogs();

    public RPGame(){

    }


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
