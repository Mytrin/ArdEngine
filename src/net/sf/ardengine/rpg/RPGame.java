package net.sf.ardengine.rpg;

import net.sf.ardengine.Core;
import net.sf.ardengine.IGame;
import net.sf.ardengine.dialogs.Dialogs;

/**
 * Game class with set up RPG features.
 */
public abstract class RPGame implements IGame {

    /**Game logic handler*/
    private ANetworkCore logicalCore;
    /**Dialog loader*/
    protected Dialogs dialogs = new Dialogs();

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
