package net.sf.ardengine.rpg.multiplayer;

/**
 * Functional interface for AServerCore.addDelayedTask()
 */
@FunctionalInterface
public interface IDelayedAction {

    /**Code to execute in future*/
    public void execute();
}
