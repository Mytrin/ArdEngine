package net.sf.ardengine.renderer.opengl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_POLYGON_SMOOTH_HINT;
import static org.lwjgl.opengl.GL11.glHint;

import java.io.InputStream;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ardengine.Core;
import net.sf.ardengine.Group;
import net.sf.ardengine.IDrawable;
import net.sf.ardengine.logging.Config;
import net.sf.ardengine.renderer.opengl.legacy.LegacyOpenGLRenderer;
import net.sf.ardengine.renderer.opengl.lib.GLUtils;
import net.sf.ardengine.renderer.opengl.lib.text.STBFont;
import net.sf.ardengine.renderer.opengl.lib.textures.TextureManager;
import net.sf.ardengine.renderer.opengl.modern.ModernOpenGLRenderer;
import net.sf.ardengine.renderer.*;
import net.sf.ardengine.text.IFont;

import org.lwjgl.Version;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;

public abstract class OpenGLRenderer implements IRenderer, Runnable{
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	/**Input manager*/
	protected CallbackInput inputManager;
	/**Window handle*/
	protected OpenGLWindow window;

	/**Fale if game loop should stop*/
	private boolean running = true;

    /**
     * Default FPS
     */
	public static int FPS = 60; //default
	private static double TIME_STEP = 1000.0/FPS; //JavaFX has 60FPS
	private static int CORRECTION = 1000%FPS; //Thread.wait() needs long, but we need decimal
	/**Minimal period between frames in ms. Maximal FPS = 125*/
	public static final int MINIMAL_TIME_STEP = 8;
	private long testFPStime = 0;
	private int averageFps = 0;
	private int currentFPScount = 0;
	
	private static final boolean DEBUG = true;

    protected boolean failedSetup = false;

	/**
	 * Creates renderer instance and starts GL thread.
	 * 
	 * @param width Width of new window
	 * @param height Height of new window
	 */
	public OpenGLRenderer(int width, int height) {
		LOGGER.info("LWJGL version: " + Version.getVersion());
		
		if (glfwInit()) {
			inputManager = new CallbackInput();
            window = new OpenGLWindow(width, height);
            window.create(inputManager, getMajorVersion(), getMinorVersion());

            if (window.isCreated()) {
                GL.createCapabilities();

                LOGGER.info("OpenGL: " + GL11.glGetString(GL11.GL_VERSION));

				//startup config
				Config.Quality configQuality = Config.getQuality("shape-quality", Config.Quality.NORMAL);
				int shapeQuality = GL_NICEST;

				if(configQuality != Config.Quality.BEST){
					if(configQuality == Config.Quality.FAST){
						shapeQuality = GL_FASTEST;
					}else{
						shapeQuality = GL_DONT_CARE;
					}
				}

				glHint(GL_POINT_SMOOTH_HINT, shapeQuality);
				glHint(GL_LINE_SMOOTH_HINT, shapeQuality);
				glHint(GL_POLYGON_SMOOTH_HINT, shapeQuality);
            }else{
                glfwTerminate();
                failedSetup = true;
            }
		}else{
            failedSetup = true;
        }
	}
	
	private long updateStart;
	private long nextUpdateTime;
	private long delta;
	private int correctionBuf = 0;
	
	@Override
	public void run() {
		updateStart = System.currentTimeMillis();
				
		while(running){
	  			try{
		  			delta = System.currentTimeMillis() - updateStart;
		  			updateStart += delta;
		  			nextUpdateTime = updateStart + (long)TIME_STEP;	  			
		  			
		  			correctionBuf += CORRECTION;
		  			if(correctionBuf >= FPS){
		  				correctionBuf -= FPS;
		  				nextUpdateTime += 1;
		  			}
		  			
		  			testFPStime += delta;
		  			currentFPScount++;
		  			if(testFPStime >= 1000){
		  				testFPStime -= 1000;
		  				averageFps = currentFPScount;
		  				currentFPScount = 0;
		  			}
		  			
		  			updateInput();
		  			
		  			Core.run(delta);

		  			Thread.sleep((Math.max(nextUpdateTime - System.currentTimeMillis(), MINIMAL_TIME_STEP)));
		  			
	  			}catch(Exception e){
					LOGGER.log(Level.SEVERE, "Exception at IRenderer.run()!", e);
	  			}
		}
	}
	
	/**
	 * @param glMode chosen OpenGL context
	 * @param width Width of new window
	 * @param height Height of new window
	 */
	public static void setupRenderer(Renderer glMode, int width, int height){
        try{
            OpenGLRenderer renderer;

            if(glMode == Renderer.GL){
				renderer = new ModernOpenGLRenderer(width, height);
            }else{
                renderer = new LegacyOpenGLRenderer(width, height);
            }

            if(!renderer.failedSetup) {
                Core.notifyWhenRendererReady(renderer, RendererState.READY);
                renderer.run();
            }else{
                Core.notifyWhenRendererReady(null, RendererState.FAILED);
            }

        }catch(Throwable e){
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
            Core.notifyWhenRendererReady(null, RendererState.FAILED);
        }
	}
	
	protected void updateInput(){
		glfwPollEvents();
			
		if(window.closeRequested()){
            Core.exit();
		}
	}
	
	@Override
	public void render(List<IDrawable> drawables) {
			window.clear();

			for(IDrawable drawable: drawables){
				drawable.draw();
			}
			
			if(DEBUG){
				GLUtils.checkForGLErrors();
			}
	}

	@Override
	public void afterRender() {
		window.afterRender();
	}

	@Override
	public void cleanUp() {
		running = false;
		if(window!= null) window.destroy();
		glfwTerminate();
	}
	
	@Override
	public void childAdded(IDrawable child, int index) {
        //nothing to do, rendering from given list
    }
	
	@Override
	public void removeChildren(List<IDrawable> children) {
        //nothing to do, rendering from given list
    }

	@Override
	public void destroyChildren(List<IDrawable> children) {
		children.forEach((child)->child.getImplementation().free());
	}

    @Override
    public void childMoved(IDrawable child, int index) {
        //nothing to do, rendering from given list
    }

    @Override
	public IFont loadFont(InputStream font, int size, char[] specialCharacters) {
        STBFont fontImpl = new STBFont(font,  size, specialCharacters);
        TextureManager.getInstance().loadSTBFont(fontImpl);
		return fontImpl;
	}
	
	@Override
	public IFont loadFont(String fontName, int size, char[] specialCharacters) {
        STBFont fontImpl = new STBFont(fontName,  size, specialCharacters);
        TextureManager.getInstance().loadSTBFont(fontImpl);
        return fontImpl;
	}

	@Override
	public IGroupImpl createGroupImplementation(Group parentGroup) {
		return new OpenGLGroupImpl(parentGroup);
	}

	@Override
	public int getAverageFPS() {
		return averageFps;
	}

	@Override
	public int getDesiredFPS() {
		return FPS;
	}

	@Override
	public int getWindowHeight() {
		if(window != null) return window.getRealHeight();
		return 0;
	}

    @Override
    public int getBaseWindowHeight() {
        if(window != null) return window.getBaseHeight();
        return 0;
    }

    @Override
	public void setWindowHeight(int height) {
		if(window != null) window.setSize(window.getBaseWidth(), height);
		viewMatrixChange();
	}
	
	@Override
	public int getWindowWidth() {
		if(window != null) return window.getRealWidth();
		return 0;
	}

    @Override
    public int getBaseWindowWidth() {
        if(window != null) return window.getBaseWidth();
        return 0;
    }

    @Override
	public void setWindowWidth(int width) {
		if(window != null) window.setSize(width, window.getBaseHeight());
		viewMatrixChange();
	}
	
	@Override
	public void setWindowSize(int width, int height) {
		if(window != null) window.setSize(width, height);
		viewMatrixChange();
	}

	protected abstract void viewMatrixChange();

	@Override
	public String getWindowTitle() {
		if(window != null) return window.getTitle();
		return IRenderer.DEFAULT_WINDOW_TITLE;
	}
	
	@Override
	public void setWindowTitle(String title) {
		if(window != null) window.setTitle(title);
	}
	
	@Override
	public boolean isWindowResizeable() {
		if(window != null) return window.isResizeable();
		return false;
	}
	
	@Override
	public void setWindowResizeable(boolean resize) {
		if(window != null) window.setResizeable(resize);
	}
	
	@Override
	public void setDesiredFPS(int desiredFPS) {
		FPS = desiredFPS;
		TIME_STEP = 1000.0/FPS;
		CORRECTION = 1000%FPS;
	}

	public abstract int getMajorVersion();
	public abstract int getMinorVersion();

    @Override
    public long getDelta() {
        return delta;
    }

    @Override
    public float getMouseX() {
        return inputManager.getMouseX();
    }

    @Override
    public float getMouseY() {
        return inputManager.getMouseY();
    }

    @Override
    public void setMouseImage(String url) {
        if(window != null) window.setMouseImage(url);
    }

    @Override
    public void setMouseImage(InputStream is) {
        if(window != null) window.setMouseImage(is);
    }
}