package net.sf.ardengine.rpg;

import net.sf.ardengine.World;

/**
 * LoadingScreen is World rendered by Game when it is loading new Level.
 */
public abstract class ALoadingScreen extends World {

    /**
     * Called after Core starts loading level and adds this world to Game.
     */
    public abstract void start();

}
