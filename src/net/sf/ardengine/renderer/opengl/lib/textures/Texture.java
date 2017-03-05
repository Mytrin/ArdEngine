package net.sf.ardengine.renderer.opengl.lib.textures;

import static org.lwjgl.opengl.GL11.*;

import java.nio.ByteBuffer;

import net.sf.ardengine.io.Config;
import org.lwjgl.opengl.GL12;


public class Texture {
	/**GL id for this texture*/
	private int id;
	/**Texture Manager id for this texture*/
	private final String key;
	
	/**Width of texture*/
	protected final int width;
	/**Height of texture*/
	protected final int height;
	
	/**How many objects use this Texture*/
	private int usage = 1;
	/**
	 * @param width Width of texture
	 * @param height Height of texture
	 * @param data texture content
	 * @param format texture type
	 */
	public Texture(String key, int width, int height, ByteBuffer data, TextureFormat format) {
		this.key = key;
		this.width = width;
		this.height = height;
		
		id = glGenTextures();
		bind();

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);

        Config.Quality textureQuality = Config.getQuality("texture-quality", Config.Quality.NORMAL);

		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, (textureQuality!= Config.Quality.FAST)?GL_LINEAR:GL_NEAREST);
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, (textureQuality!= Config.Quality.FAST)?GL_LINEAR:GL_NEAREST);
		
		glTexImage2D(GL_TEXTURE_2D, 0, format.getInternalFormat(), width, height, 0, format.getFormat(), format.getType(), data);
	}
	
	/**
	 * @return Width of texture
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * @return Height of texture
	 */
	public int getHeight() {
		return height;
	}	
	
	/**
	 * Binds texture to selected texture unit, if on GLFW thread
	 */
	public void bind(){
		glBindTexture(GL_TEXTURE_2D, id);
	}
	
	/**
	 * Deletes texture from GL context, withot consulting TextureManager
	 */
	public void free(){
		glDeleteTextures(id);
	}
	
	/**
	 * Informs TextureManager to free this texture.
	 */
	public void delete(){
		usage--;
		if(usage <= 0){
			TextureManager.getInstance().deleteTexture(key);
		}
	}
	
	/**
	 * @return How many objects use this Texture
	 */
	int getUsage() {
		return usage;
	}
	
	/**
	 * Notifies Texture, that it's shared between less objects
	 */
	void lowerUsage(){
		usage--;
	}
	
	/**
	 * Notifies Texture, that it's shared between more objects
	 */
	public void increaseUsage(){
		usage--;
	}
	
	/** 
	 * @return Key used for identification by TetureManager (name)
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return ID of texture for framebuffer
     */
	public int getId() {
		return id;
	}
}