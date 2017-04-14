package net.sf.ardengine.renderer.opengl;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glClearColor;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;

import net.sf.ardengine.renderer.IRenderer;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFWWindowPosCallback;
import org.lwjgl.glfw.GLFWWindowSizeCallback;
import org.lwjgl.opengl.GL11;

import org.lwjgl.glfw.GLFWVidMode;

public class OpenGLWindow {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

	/** glfw window handle */
	private long windowId;
	/** Window is created flag */
	protected boolean created = false;
	/** Window title */
	protected String title = IRenderer.DEFAULT_WINDOW_TITLE;
	/** Window display width */
	private int baseWidth;
	/** Window display height */
	private int baseHeight;
	/**Real window width*/
	private int realWidth;
	/**Real window height*/
	private int realHeight;
	/** Is current window resizeable? */
	private boolean isResizeable = true;
	/** Flag for updating GL setup, when window was resized */
	private boolean recentlyResized = false;
	/**
	 * Flag for returning window to default size, when it's resized without
	 * permission
	 */
	private boolean needsDefaultResize = false;

	/* WINDOW CALLBACKS */
	private GLFWWindowPosCallback winPosCallback;
	private GLFWWindowSizeCallback winSizeCallback;

	/**
	 * Creates new OpenGL window instance, however succesful create() call is
	 * required to display window.
	 * 
	 * @param baseWidth  Window display width
	 * @param baseHeight Window display height
	 */
	public OpenGLWindow(int baseWidth, int baseHeight) {
		this.baseWidth = baseWidth;
		this.baseHeight = baseHeight;
        this.realWidth = baseWidth;
        this.realHeight = baseHeight;
	}

	/**
	 * GLFW window initialization
	 * 
	 * @param inputManager class responsible for input handling
	 * @param majorVersion Opengl major version(1-4) (Major --> 3.1 <-- Minor)
	 * @param minorVersion OpenGL minor version (1-5)
	 */
	public void create(CallbackInput inputManager, int majorVersion, int minorVersion) {
		if (created) {
			LOGGER.warning("Called create() on created OpenGLWindow");
			return;
		}

		glfwDefaultWindowHints();
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, majorVersion);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, minorVersion);
		glfwWindowHint(GLFW_VISIBLE, GL11.GL_FALSE); // the window will stay
																	// hidden after creation
		glfwWindowHint(GLFW_RESIZABLE, isResizeable ? GL11.GL_TRUE : GL11.GL_FALSE);

		windowId = glfwCreateWindow(baseWidth, baseHeight, IRenderer.DEFAULT_WINDOW_TITLE + " - OpenGL", NULL, NULL);

		if (windowId == NULL) {
			created = false;
			return;
		}
		GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

		// Center our window
		glfwSetWindowPos(windowId, (vidmode.width() - baseWidth) / 2, (vidmode.height() - baseHeight) / 2);

		// Callbacks

        winSizeCallback = GLFWWindowSizeCallback.create((window, width, height) -> {
            // GLFW can't change property of once created window,
            // so I created event, which returns window to
            // default state after resizing.
            recentlyResized = true;
            if (!isResizeable) {
                needsDefaultResize = true;
            }else{
                realWidth = width;
                realHeight = height;
            }
        });

		glfwSetWindowSizeCallback(windowId, winSizeCallback);

		winPosCallback = GLFWWindowPosCallback.create((long window, int xpos, int ypos)  -> {
				// In case window position would be needed
		});

		glfwSetWindowPosCallback(windowId, winPosCallback);
		if (inputManager != null) {
			inputManager.init(windowId);
		}

		glfwMakeContextCurrent(windowId);
		// Swapping buffers
		glfwSwapInterval(1);

		glfwShowWindow(windowId);

		created = true;
	}

	/**
	 * Called by GL Renderers to swapBuffers etc.
	 */
	public void afterRender() {
		glfwSwapBuffers(windowId);
	}

	/**
	 * Clears window screen
	 */
	public void clear() {
		handleResize();

		glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
		glClear(GL_COLOR_BUFFER_BIT);
	}

	private void handleResize() {
		if (recentlyResized) {
			if (!needsDefaultResize) {
				IntBuffer width = BufferUtils.createIntBuffer(1);
				IntBuffer height = BufferUtils.createIntBuffer(1);

				glfwGetWindowSize(windowId, width, height);

				GL11.glViewport(0, 0, width.get(0), height.get(0));
			} else {
				glfwSetWindowSize(windowId, baseWidth, baseHeight);
				needsDefaultResize = false;
			}
			recentlyResized = false;
		}
	}

	/**
	 * @return true if window is already created
	 */
	public boolean isCreated() {
		return created;
	}

	/**
	 * @return true if glfwWindowShouldClose() returns true
	 */
	public boolean closeRequested() {
		return glfwWindowShouldClose(windowId);
	}

	/**
	 * Calls glfwDestroyWindow()
	 */
	public void destroy() {
        glfwFreeCallbacks(windowId);
        glfwDestroyWindow(windowId);
	}

	/**
	 * @return Projection(not actual) height of window
	 */
	public int getBaseHeight() {
		return baseHeight;
	}

    /**
     * @return actual height of window
     */
    public int getRealHeight() {
        return realHeight;
    }

    /**
	 * @return Projection(not actual) width of window
	 */
	public int getBaseWidth() {
		return baseWidth;
	}

    /**
     * @return actual height of window
     */
    public int getRealWidth() {
        return realWidth;
    }

	/**
	 * Allows this window to be resized, otherwise it ignores the resize
	 * callback.
	 * 
	 * @param isResizeable should the window be resizeable?
	 */
	public void setResizeable(boolean isResizeable) {
		this.isResizeable = isResizeable;
	}

	/**
	 * @return true if the window is resizeable
	 */
	public boolean isResizeable() {
		return isResizeable;
	}

	/**
	 * Sets projection and actual size of this window
	 * 
	 * @param baseWidth new width
	 * @param baseHeight new height
	 */
	public void setSize(int baseWidth, int baseHeight) {
		this.baseWidth = baseWidth;
		this.baseHeight = baseHeight;

		if (!created)
			return;

		glfwSetWindowSize(windowId, baseWidth, baseHeight);
	}

	/**
	 * @return Title of this window
	 */
	public String getTitle() {
		return title;
	}

	/**
	 * Changes title of the window
	 * 
	 * @param title new title of the window
	 */
	public void setTitle(String title) {
		this.title = title;

		if (!created)
			return;

		glfwSetWindowTitle(windowId, title);
	}

    /**
     * Changes look of mouse cursor
     * @param url - path to source image
     */
    public void setMouseImage(String url) {
        try{
            OpenGLUtil.setMouseImage(url, windowId);
        }catch(Exception e){
            LOGGER.log(Level.WARNING, "Loading mouse cursor: ", e);
        }
    }

    /**
     * Changes look of mouse cursor
     * @param is - source to source image
     */
    public void setMouseImage(InputStream is) {
        try{
            OpenGLUtil.setMouseImage(is, windowId);
        }catch(Exception e){
            LOGGER.log(Level.WARNING, "Loading mouse cursor: ", e);
        }
    }
}
