package net.sf.ardengine.renderer;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

import net.sf.ardengine.*;
import net.sf.ardengine.shapes.IShape;
import net.sf.ardengine.shapes.IShapeImpl;
import net.sf.ardengine.text.IFont;
import net.sf.ardengine.text.ITextImpl;
import net.sf.ardengine.text.Text;

/**
 * 
 * @author Mytrin
 *
 * Renderer uses given library to draw given objects.
 * This class also defines, which methods are common for all renderers.
 *
 */
public interface IRenderer {

	public static final String DEFAULT_WINDOW_TITLE = "ArdEngine window";
	
	/**
	 * Renders all children.
	 * 
	 * @param drawables - list of IDrawablea to draw
	 */
	public void render(List<IDrawable> drawables);

    /**
     * Renders collisions atop of screen, please note,
     * that this method may not be supportd by all renderers.
     * @param collideables - list of nodes with collisons to display
     */
    public void renderCollisions(List<Node> collideables);

	/**
	 * Called from Core after finished render() and render*()
	 */
	public void afterRender();

    /**
     * @return milliseconds between last frame and this
     */
    public long getDelta();

	/**
	 * Called when exiting application to free resources.
	 */
	public abstract void cleanUp();
	
	//WINDOW GETTERS AND SETTERS
	
	/**
	 * Sets new size of window
	 * @param width Width of window
	 * @param height Height of window
	 */
	public void setWindowSize(int width, int height);
	
	/**
	 * @param width new width of window
	 */
	public void setWindowWidth(int width);
	
	/**
	 * @return actual width of window
	 */
	public int getWindowWidth();

	/**
	 * @return virtual width of window
     */
	public int getBaseWindowWidth();

	/**
	 * @param height new height of window
	 */
	public void setWindowHeight(int height);
	
	/**
	 * @return actual height of window
	 */
	public int getWindowHeight();

    /**
     * @return virtual height of window
     */
    public int getBaseWindowHeight();

	/**
	 * @param title new title of window
	 */
	public void setWindowTitle(String title);
	
	/**
	 * @return title of window
	 */
	public String getWindowTitle();
	
	/**
	 * @return true, if window is resizeable
	 */
	public boolean isWindowResizeable();
	
	/**
	 * Makes rendered window resizeable or nonresizeable
	 * @param resize true, if window should resizeable
	 */
	public void setWindowResizeable(boolean resize);
	
   /**
    * @return Average count of updates per second;
    */
   public int getAverageFPS();
   
   /**
    * NOTE: Some renderers have hardCoded 60 FPS!
    * 
    * @param desiredFPS - desired count of updates per second.
    */
   public void setDesiredFPS(int desiredFPS);
   
   /**
    * @param buf - buffered image with sprite content
    * @param parentSprite - sprite, for which is implementation created
    * @return new Sprite implementation for this renderer
    */
   public ISpriteImpl createSpriteImplementation(BufferedImage buf, Sprite parentSprite);
   
   /**
    * @param is - input stream to source image
    * @param parentSprite - sprite, for which is implementation created
    * @return new Sprite implementation for this renderer
    */
   public ISpriteImpl createSpriteImplementation(InputStream is, Sprite parentSprite);
   
   /**
    * @param url - path to source image
    * @param parentSprite - sprite, for which is implementation created
    * @return new Sprite implementation for this renderer
    */
   public ISpriteImpl createSpriteImplementation(String url, Sprite parentSprite);

    /**
     * @param buf - buffered image with sprite content
     * @param parentSprite - sprite, for which is implementation created
     * @return new SpriteSheet implementation for this renderer
     */
    public ISpriteSheetImpl createSpriteSheetImplementation(BufferedImage buf, SpriteSheet parentSprite);

    /**
     * @param is - input stream to source image
     * @param parentSprite - sprite, for which is implementation created
     * @return new SpriteSheet implementation for this renderer
     */
    public ISpriteSheetImpl createSpriteSheetImplementation(InputStream is, SpriteSheet parentSprite);

    /**
     * @param url - path to source image
     * @param parentSprite - sprite, for which is implementation created
     * @return new SpriteSheet implementation for this renderer
     */
    public ISpriteSheetImpl createSpriteSheetImplementation(String url, SpriteSheet parentSprite);
   
   /**
    * @param shape  shape, for which is implementation created
    * @return new Shape implementation for given shape
    */
   public IShapeImpl createShapeImplementation(IShape shape);
   
   /**
    * @param font - font, which should text use
    * @param parentText - text, for which is implementation created
    * @return new Text implementation for this renderer
    */
   public ITextImpl createTextImplementation(IFont font, Text parentText);

	/**
	 * @param parentGroup - group, for which is implementation created
	 * @return new Group implementation for this renderer
	 */
	public IGroupImpl createGroupImplementation(Group parentGroup);
   
	 /**
    * @param fontName name of font from java to load 
    * @param size  Size of font
    * @param specialCharacters language dependent characters to load
    * @return font implementation for this renderer
    */
   public IFont loadFont(String fontName, int size, char[] specialCharacters);
   
	 /**
    * @param font Font source
    * @param size  Size of font
    * @param specialCharacters language dependent characters to load
    * @return font implementation for this renderer
    */
   public IFont loadFont(InputStream font, int size, char[] specialCharacters);

   /**
    * Automatically called by Core, when new child is added to renderer
    * @param child added drawable
    * @param index of drawable
    */
   public void childAdded(IDrawable child, int index);
   
   /**
    * Periodically called by Core, removes children from scene, if required
    * @param removedChildren removed drawables
    */
   public void removeChildren(List<IDrawable> removedChildren);

	/**
	 * Periodically called by Core, invokes free() on removed chldren
	 * @param destroyedChildren removed drawables
	 */
	public void destroyChildren(List<IDrawable> destroyedChildren);

    /**
     * Automatically called whenever IDrawable changes Z Coord
     * @param child modified drawable
     * @param index new index
     */
    public void childMoved(IDrawable child, int index);

    //INPUT

    /**
     * @return REAL X coord of mouse in window
     */
    public float getMouseX();

    /**
     * @return REAL Y coord of mouse in window
     */
    public float getMouseY();

	/**
	 * Changes look of mouse cursor
	 * @param is - input stream to source image
	 */
	public void setMouseImage(InputStream is);

	/**
	 * Changes look of mouse cursor
	 * @param url - path to source image
	 */
	public void setMouseImage(String url);
}