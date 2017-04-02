package net.sf.ardengine;

import net.sf.ardengine.animations.AnimationManager;
import net.sf.ardengine.events.*;
import net.sf.ardengine.input.InputManager;
import net.sf.ardengine.io.ArdLogger;
import net.sf.ardengine.io.Config;
import net.sf.ardengine.io.console.ConsoleUI;
import net.sf.ardengine.renderer.javaFXRenderer.JavaFXRenderer;
import net.sf.ardengine.renderer.opengl.OpenGLRenderer;
import net.sf.ardengine.renderer.IRenderer;
import net.sf.ardengine.renderer.Renderer;
import net.sf.ardengine.renderer.RendererState;
import net.sf.ardengine.renderer.opengl.lib.text.STBFont;
import net.sf.ardengine.sound.SoundManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * Used Libraries:  LWJGL3( + GLFW, OpenAL, STB)
 *
 *                  Paul's Sound system (+ CodeclBXM, CodecJOGG, CodecWAW) - http://www.paulscode.com
 *
 *                  "This software is based on or using the J-Ogg library available from
 *                  http://www.j-ogg.de and copyrighted by Tor-Einar Jarnbjo."
 *
 * Licences are located next to the libraries at lib folder.
 * 
 * The main class of whole game, here are located methods used
 * for game updates(collisions, controls) and configuration(camera, renderer selection).
*/
public class Core {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	/**Version of library*/
	public static String VERSION = "Buildable";
	/**True, if this build is experimental*/
	public static final boolean DEBUG = true;
    /**True, if game currently runs in debugging mode*/
    public static boolean debugMode = false;

	/**Renderer for content managing and drawing.*/
	public static IRenderer renderer;

    /**Game class*/
    public static IGame game;
    /**Active game world*/
    private static World world;

	/**Global X translation for all object with non static coords*/
    public static float cameraX = 0;
    /**Global Y translation for all object with non static coords*/
    public static float cameraY = 0;

    /** Is game loop running? */
    public static boolean gameRunning = true;
    /** Is window paused?(for window minimalization) */
    public static boolean freeze = false;

    /**All currently updated nodes*/
    protected static List<Node> nodes = new ArrayList<>();

    /**All currently rendered drawables*/
	protected static List<IDrawable> drawables = new LinkedList<>();
    /**Drawables marked for removal in next loop*/
	private static List<IDrawable> drawablesToRemove = Collections.synchronizedList(new LinkedList<>());
    /**Drawables marked for destroying in next loop*/
    private static List<IDrawable> drawablesToDestroy = Collections.synchronizedList(new LinkedList<>());
	/**Drawables marked for adding in next loop*/
	private static List<IDrawable> drawablesToAdd = Collections.synchronizedList(new LinkedList<>());

   /**
    * Starts automatically selected renderer initialization.
    * @param game Game main class
    */
	public static final void start(IGame game){
        start(game, Renderer.DONT_CARE);
	}
	
   /**
    * Starts renderer initialization.
    * @param chosenRenderer - Render used for rendering
    * @param game Game main class
    */
	public static final void start(IGame game, Renderer chosenRenderer){
        Core.game = game;

        Config.loadDefaultConfig();

		ArdLogger.initLogger(DEBUG?Level.ALL:Level.INFO);
		LOGGER.info("Selected renderer: "+chosenRenderer.name());
		
		switch(chosenRenderer){
			case JAVAFX: JavaFXRenderer.createRenderer(800, 600); break;
			case GL: OpenGLRenderer.setupRenderer(Renderer.GL, 800, 600); break;
			case LEGACY_GL: OpenGLRenderer.setupRenderer(Renderer.LEGACY_GL, 800, 600); break;
			case DONT_CARE: OpenGLRenderer.setupRenderer(Renderer.GL, 800, 600); break;
			default: JavaFXRenderer.createRenderer(800, 600); break;
		}

		//In case of failure or JavaFX
        destroy();
        System.exit(1);
	}
	
	/**
	 * Used automatically, calls gameInit(), 
	 * @param newRenderer related renderer, which sends info or null
     * @param state state of renderer initialization
	 */
	public static final void notifyWhenRendererReady(IRenderer newRenderer, RendererState state){
		if(state == RendererState.READY){
			renderer = newRenderer;
			LOGGER.info("Initialization of selected renderer has been successful!");
			init();
		}else if(state == RendererState.FAILED){
			LOGGER.severe("Initialization of selected renderer failed, falling to JavaFX!");
            JavaFXRenderer.createRenderer(800, 600);
		}else{
			LOGGER.severe("Initialization of fallback renderer failed!");
		}
	}
	
	/**
	 * Called after start() to initialize engine components.
	 */
	public static final void init(){
        InputManager.getInstance().setMouseImage(Core.class.getClassLoader().getResourceAsStream("net/sf/ardengine/res/cursor.png"));

        Sprite background = game.getInitBackground();
        addDrawable(background);

        clearDrawableLists();
        renderer.render(drawables); //render before expensive sound init
        renderer.afterRender();

        FontManager.loadFont(Core.class.getClassLoader().getResourceAsStream("net/sf/ardengine/res/fonts/fira-sans/FiraSans-Regular.ttf"),
                FontManager.DEFAULT_KEY, 12, STBFont.CZECH_CHARACTERS);

        ConsoleUI.init();

        SoundManager.init((renderer instanceof OpenGLRenderer)?Renderer.GL:Renderer.JAVAFX);

        removeDrawable(background);
        game.gameInit();
	}

    /**
     * game loop
     *
     * @param delta Time passed since last update (ms)
     */
    public static final void run(long delta) {
        if(gameRunning){
            if(!freeze){
                //events -> node logic -> animations
                InputManager.getInstance().update();

                if(world != null) world.clearLists();
                clearDrawableLists();

                updateCollisions();

                nodes.forEach(((node)->node.updateLogic()));

                AnimationManager.animate();

                game.gameRun();
                if(world != null) world.run();

                renderer.render(drawables);
                if(debugMode){
                    renderer.renderCollisions(nodes);
                }
                renderer.afterRender();
            }
        }else{
            destroy();
            System.exit(0);
        }
    }

    /**
     * Periodically called from run,
     * inserts drawables to main list.
     */
    private static void clearDrawableLists(){
        //REMOVING MUST BE BEFORE ADDING TO GENERATE CORRECT Z
        for(IDrawable drawable : drawablesToRemove){
            if(drawable instanceof Node){
                nodes.remove((Node)drawable);
                ((Node) drawable).invokeEvent(new RemovalEvent());
            }
            drawables.remove(drawable);
        }
        renderer.removeChildren(drawablesToRemove);
        drawablesToRemove.clear();

        for(IDrawable drawable : drawablesToDestroy){
            if(drawable instanceof Node){
                nodes.remove((Node)drawable);
                ((Node) drawable).invokeEvent(new RemovalEvent());
            }
            drawables.remove(drawable);
        }
        renderer.destroyChildren(drawablesToDestroy);
        drawablesToDestroy.clear();

        for(IDrawable newDrawable : drawablesToAdd){
            insertDrawable(newDrawable);
        }
        drawablesToAdd.clear();

    }

    /**
     * Iterates trough game nodes and invokes CollisionEvents
     */
    private static void updateCollisions(){
        for(int i=0; i < nodes.size(); i++){
            Node collideable = nodes.get(i);
            for(int j=i+1; j < nodes.size(); j++){
                Node possibleIntruder = nodes.get(j);

                if(collideable.mayIntersectWith(possibleIntruder)){
                    collideable.eventIfCollidesWith(possibleIntruder);
                }
            }
        }
    }

    private static int findZOrderPosition(IDrawable drawable){
        double drawableZ = drawable.getZ();
        int maxIndex = drawables.size();
        int minIndex = 0;
        int index = maxIndex/2;
        boolean found = false;

        while(!found && minIndex != maxIndex){ //minIndex == maxIndex => end or start of array
            if(drawables.get(index).getZ() < drawableZ){
                minIndex = index + 1;
                index = (index + maxIndex) / 2; // =>
            }else if(drawables.get(index).getZ() > drawableZ){
                maxIndex = index;
                index = (minIndex + maxIndex) / 2; // <=
            }else{
                found = true;

                //find last drawable with such Z
                for(int i=index; i < drawables.size(); i++){
                    if(drawables.get(i).getZ() != drawableZ){
                        break;
                    }
                    index = i;
                }

            }
        }
        index+=1;

        return index;
    }

	/**
	 * Inserts drawable to drawing list so it does not have to be sorted by Z, which is useful for some renderers(OpenGL)
	 * @param drawable drawable to insert to list
	 */
   private static void insertDrawable(IDrawable drawable){
  		 int index = findZOrderPosition(drawable);

       if(index>=drawables.size()){
           //Please note that childAdded must be before drawables.add()!
            renderer.childAdded(drawable, drawables.size());
            drawables.add(drawable); //add to end of ArrayList
     	 }else{
     		drawables.add(index, drawable); 
     		renderer.childAdded(drawable, index);
     	 }

        if(drawable instanceof Node){
            nodes.add((Node)drawable);
        }
   }

    /**
     * Automatically called by node, when its Z has been changed
     * @param drawable node with changed Z
     */
    public static void childrenZChanged(IDrawable drawable){
        if(!drawables.remove(drawable)){
            //Great, Z was changed before children got to drawables list,
            //so none extra calculations required
            return;
        }

        int index = findZOrderPosition(drawable);
        if(index>=drawables.size()) index = drawables.size();

        drawables.add(index, drawable);
        renderer.childMoved(drawable, index);
    }

	/**
    * Disposes resources before exit.
    */
	protected static final void destroy() {
        game.gameCleanUp();

        renderer.removeChildren(drawables);
		renderer.cleanUp();

        SoundManager.cleanUp();
    }

    /**
     * Cleans resources and closes window
     */
	public static void exit(){
        gameRunning = false;
    }

	/**
	 * Starts rendering drawable every loop
	 * @param drawable drawable to add
	 */
	public static void addDrawable(IDrawable drawable){
        drawablesToAdd.add(drawable);
	}

    /**
     * @return Count of drawables at this loop
     */
    public static int getDrawableCount(){
        return drawables.size();
    }

    /**
     * @return Count of drawables at this loop
     */
    public static int getNodeCount(){
        return nodes.size();
    }

    /**
     * DO NOT USE THIS METHOD TO INSERT NEW NODES!
     * For such purposes exists addDrawable()
     *
     * @return list of game nodes
     */
    public static List<Node> getNodes() {
        List<Node> saveCopy = new LinkedList<>();
        saveCopy.addAll(nodes);
        return saveCopy;
    }

    /**
     * Removes drawable from rendered ones and destroys its resources
     * @param drawable drawable to remove
     */
    public static void removeDrawable(IDrawable drawable){
        removeDrawable(drawable, true);
    }

    /**
	 * Removes drawable from rendered ones
	 * @param drawable drawable to remove
     * @param destroy free resources allocated by drawable implementation(drawable won't be usable again)
	 */
	public static void removeDrawable(IDrawable drawable, boolean destroy){
		drawablesToRemove.add(drawable);
        if(destroy) drawablesToDestroy.add(drawable);
	}

    /**
     * Changes active world and frees resources of last one.
     * @param world new world
     */
    public static void setWorld(World world) {
        setWorld(world, true);
    }

    /**
     * Changes active world
     * @param world new world
     * @param clean true, if last World recources should be freed
     */
    public static void setWorld(World world, boolean clean) {
        if(Core.world!= null && clean) Core.world.cleanUp();

        Core.world = world;
    }

    /**
     * @return Active world
     */
    public static World getWorld() {
        return world;
    }

    public static void main(String[] args) {
		//start(new TestGame(), Renderer.GL);
        //start(new TestGame(), Renderer.LEGACY_GL);
		//start(new TestGame(), Renderer.JAVAFX);
        start(new TestGame(), Renderer.DONT_CARE);
	}
}