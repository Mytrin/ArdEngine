package net.sf.ardengine.renderer.opengl;

import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;

import static org.lwjgl.glfw.GLFW.glfwSetCursor;

/**
 * Collection of helper methods, which would only
 * take precious lines elsewhere.
 */
public class OpenGLUtil {

    /**
     * Changes look of mouse cursor
     * @param url - path to source image
     * @param windowHandle - window to change cursor to
     */
    public static void setMouseImage(String url, long windowHandle) throws Exception{
        setMouseImage(new FileInputStream(url), windowHandle);
    }

    /**
     * Changes look of mouse cursor
     * Taken from http://gamedev.stackexchange.com/questions/124394/setting-up-a-custom-cursor-image-in-lwjgl-3/124395
     * @param is - stream to source image
     * @param windowHandle - window to change cursor to
     */
    public static void setMouseImage(InputStream is, long windowHandle) throws Exception{
        BufferedImage image = ImageIO.read(is);

        int width = image.getWidth();
        int height = image.getHeight();

        int[] pixels = new int[width * height];
        image.getRGB(0, 0, width, height, pixels, 0, width);

        // convert image to RGBA format
        ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int pixel = pixels[y * width + x];

                buffer.put((byte) ((pixel >> 16) & 0xFF));  // red
                buffer.put((byte) ((pixel >> 8) & 0xFF));   // green
                buffer.put((byte) (pixel & 0xFF));          // blue
                buffer.put((byte) ((pixel >> 24) & 0xFF));  // alpha
            }
        }
        buffer.flip(); // this will flip the cursor image vertically

        // create a GLFWImage
        GLFWImage cursorImg= GLFWImage.create();
        cursorImg.width(width);     // setup the image width
        cursorImg.height(height);   // setup the image height
        cursorImg.pixels(buffer);   // pass image data

        // create custom cursor and store its ID
        int hotspotX = 0;
        int hotspotY = 0;
        long cursorID = GLFW.glfwCreateCursor(cursorImg, hotspotX , hotspotY);

        // set current cursor
        glfwSetCursor(windowHandle, cursorID);
    }

}