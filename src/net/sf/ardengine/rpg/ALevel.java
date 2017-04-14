package net.sf.ardengine.rpg;

import net.sf.ardengine.AWorld;

import java.io.File;

/**
 *  Represents one level of a multiplayer game.
 *  Abstract methods has to be implemented in order
 *  for multiplayer function properly.
 */
public abstract class ALevel extends AWorld {

    /**
     * Called after Core loads this level and adds it to Game.
     */
    public abstract void start();

    /**
     * For ServerCore
     * @return file with current state of level
     */
    public abstract File getSourceFile();

}
