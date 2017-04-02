package net.sf.ardengine;

/**
 * Interface for classes to obtain Game features.
 */
public interface IGame {

    //GAME methods

    /**
     * For user, overridable method called when init() is finished.
     */
    public void gameInit();

    /**
     * For user, overridable method called every game loop
     */
    public void gameRun();

    /**
     * Overridable method for cleanup of user's resources.
     */
    public void gameCleanUp();

    //CORE setters and getters

    /**
     * Changes active world
     * @param newWorld new world
     * @param clean true, if last World recources should be freed
     */
    public default void setWorld(World newWorld, boolean clean){
        Core.setWorld(newWorld, clean);
    }

    /**
     * Changes active world and frees resources of last one.
     * @param newWorld new world
     */
    public default void setWorld(World newWorld){
        Core.setWorld(newWorld);
    }

    public default World getWorld(){
        return Core.getWorld();
    }

    /**
     * @param cameraX Global X translation for all object with non static coords
     */
    public default void setCameraX(float cameraX){
        Core.cameraX = cameraX;
    }

    /**
     * @param cameraY Global Y translation for all object with non static coords
     */
    public default void setCameraY(float cameraY){
        Core.cameraX = cameraY;
    }

    /**
     * @return Global X translation for all object with non static coords
     */
    public default float getCameraX(){
        return Core.cameraX;
    }

    /**
     * @return Global Y translation for all object with non static coords
     */
    public default float getCameraY(){
        return Core.cameraY;
    }

    /**
     * Starts rendering drawable every loop
     * @param drawable drawable to add
     */
    public default void addDrawable(IDrawable drawable){
        Core.addDrawable(drawable);
    }

    /**
     * Removes drawable from rendered ones and destroys its resources
     * @param drawable drawable to remove
     */
    public default void removeDrawable(IDrawable drawable){
        Core.removeDrawable(drawable);
    }

    /**
     * Removes drawable from rendered ones
     * @param drawable drawable to remove
     * @param destroy free resources allocated by drawable implementation(drawable won't be usable again)
     */
    public default void removeDrawable(IDrawable drawable, boolean destroy){
        Core.removeDrawable(drawable, destroy);
    }

    //UTILITY METHODS

    public default Sprite getInitBackground(){
        return new Sprite(Core.class.getClassLoader().getResourceAsStream("net/sf/ardengine/res/ard_bg.png"));
    }
}
