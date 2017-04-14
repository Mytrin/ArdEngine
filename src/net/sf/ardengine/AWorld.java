package net.sf.ardengine;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * AWorld serves as container for nodes and sprites, which should be loaded
 * and maintained only after fulfilling some condition(Menu - Start new game).
 */
public abstract class AWorld {
    /**AWorld drawables*/
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
     * Method called after is world removed from Core.
     * Removes all
     */
    public void cleanUp(){
        destroyDrawables(drawables);
        clearLists();
    }

    /**
     * Automatically called in Core loop, checks collisions and does  doLogic() for all nodes
     */
    protected void clearLists(){
        drawables.removeAll(drawablesToDestroy);
        drawablesToDestroy.forEach((IDrawable drawable)->Core.removeDrawable(drawable, true));
        drawablesToDestroy.clear();

        drawables.removeAll(drawablesRemoveList);
        drawablesRemoveList.forEach((IDrawable drawable)->Core.removeDrawable(drawable, false));
        drawablesRemoveList.clear();

        drawables.addAll(drawablesAddingList);
        drawablesAddingList.forEach((IDrawable drawable)->Core.addDrawable(drawable));
        drawablesAddingList.clear();
    }

    /**
     * Inserts Drawables to AWorld
     * @param drawables New drawables to display
     */
    public void addDrawables(List<IDrawable> drawables){
        drawablesAddingList.addAll(drawables);
    }

    /**
     * Inserts Drawable to AWorld
     * @param drawable New drawable to display
     */
    public void addDrawable(IDrawable drawable){
        drawablesAddingList.add(drawable);
    }

    /**
     * Safely removes Drawable from world
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

    /**
     * Safely removes Drawable from world and frees its resources
     * @param drawables drawables to remove
     */
    public void destroyDrawables(List<IDrawable> drawables){
        drawablesToDestroy.addAll(drawables);
    }

    /**
     * Safely removes Drawable from world and frees its resources
     * @param drawable - drawable to remove
     */
    public void destroyDrawable(IDrawable drawable) {
        drawablesToDestroy.add(drawable);
    }
}