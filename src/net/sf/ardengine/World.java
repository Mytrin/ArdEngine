package net.sf.ardengine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * World serves as container for nodes and sprites, which should be loaded
 * and maintained only after fulfilling some condition(Menu->Start new game).
 */
public abstract class World {
    /**World drawables*/
    List<IDrawable> drawables = new LinkedList<>();
    protected List<IDrawable> drawablesRemoveList = Collections.synchronizedList(new LinkedList<>());
    protected List<IDrawable> drawablesAddingList = Collections.synchronizedList(new LinkedList<>());
    protected List<IDrawable> drawablesToDestroy = Collections.synchronizedList(new LinkedList<>());

    /**
     * Automatically called in Core loop.
     * Overridable method for commands, which should be done only when this world is used.
     */
    public abstract void run();

    /**
     * Overridable method called after is world removed from Core
     */
    public abstract void cleanUp();

    /**
     * Automatically called in Core loop, checks collisions and does  doLogic() for all nodes
     */
    protected  synchronized void clearLists(){
        drawables.removeAll(drawablesRemoveList);

        for(IDrawable drawable : drawablesToDestroy){
            Core.removeDrawable(drawable, true);
            drawablesRemoveList.remove(drawable);
        }
        drawablesToDestroy.clear();

        for(IDrawable drawable : drawablesRemoveList){
            Core.removeDrawable(drawable, false);
        }
        drawablesRemoveList.clear();

        drawables.addAll(drawablesAddingList);
        for(IDrawable drawable : drawablesAddingList){
            Core.addDrawable(drawable);
        }
        drawablesAddingList.clear();
    }

    /**
     * Inserts Drawables to World
     * @param drawables New drawables to display
     */
    public void addDrawables(List<IDrawable> drawables){
        drawablesAddingList.addAll(drawables);
    }

    /**
     * Inserts Drawable to World
     * @param drawable New drawable to display
     */
    public void addDrawable(IDrawable drawable){
        drawablesAddingList.add(drawable);
    }

    /**
     * Inserts Drawables to World
     * @param drawables drawables to remove
     */
    public void removeDrawables(List<IDrawable> drawables){
        drawablesRemoveList.addAll(drawables);
    }

    /**
     * Safely removes Drawable from world
     * @param drawable - drawable to remove
     */
    public void removeDrawable(IDrawable drawable) {
        drawablesRemoveList.add(drawable);
    }
}