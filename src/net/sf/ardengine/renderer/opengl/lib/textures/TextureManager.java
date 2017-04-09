package net.sf.ardengine.renderer.opengl.lib.textures;

import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_info_from_memory;
import static org.lwjgl.stb.STBImage.stbi_load_from_memory;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.file.FileSystemNotFoundException;
import java.util.HashMap;
import java.util.logging.Logger;

import net.sf.ardengine.logging.Util;

import net.sf.ardengine.renderer.opengl.lib.text.STBFont;
import org.lwjgl.BufferUtils;

public class TextureManager {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private static TextureManager instance;
	protected final HashMap<String, Texture> textures = new HashMap<>();
	
	private TextureManager() {
		
	}

	/**
	 * Calls loadTexture(String name, File source)
	 * 
	 * @param path name and path to the image
	 */
	public void loadTexture(String path){
		loadTexture(path, new File(path));
	}
	
	/**
	 * Informs TextureManager to load this texture.
	 * 
	 * @param name name of the texture
	 * @param source file with the image
	 */
	public void loadTexture(String name, File source){
		if(checkIfExists(name)) return;
		
		if(source.exists()){
			ByteBuffer imageBuffer;
			try {
				imageBuffer = Util.fileToByteBuffer(source);
			} catch (IOException e) {
				LOGGER.severe("Texture loading IO error "+e);
				throw new RuntimeException(e);
			}
			new loadSTBTextureOperation(name, imageBuffer).execute();
		}else{
			LOGGER.severe("Texture source "+source.getPath()+" not found!");
			
			throw new FileSystemNotFoundException("Texture source "+source.getPath()+" not found!");
		}
	}
	
	/**
	 * Informs TextureManager to load this texture.
	 * 
	 * @param name name of the texture
	 * @param source stream to the source image
	 */
	public void loadTexture(String name, InputStream source){
		if(checkIfExists(name)) return;
		
		ByteBuffer imageBuffer;
		try {
			imageBuffer = Util.streamToByteBuffer(source, 1024);
		} catch (IOException e) {
			LOGGER.severe("Texture loading IO error "+e);
			throw new RuntimeException(e);
		}
		new loadSTBTextureOperation(name, imageBuffer).execute();
	}
		
	/**
	 * Informs TextureManager to load this texture.
	 * This method uses java.awt, instead of LWJGL version of STB.
	 * 
	 * @param name name of the texture
	 * @param img buffered image with texture content
	 */
	public void loadTexture(String name, BufferedImage img){
		if(checkIfExists(name)) return;

		new LoadBufferedImageTextureOperation(name, img).execute();
	}
	
	/**
	 * Called from loadTexture(), in case when texture exists, increases ots usage.
	 * @param textureName name of the texture
	 * @return true if texture exists
	 */
	private boolean checkIfExists(String textureName){
		Texture loadedTexture = textures.get(textureName);
		if(loadedTexture != null){
			loadedTexture.increaseUsage();
			return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @param name name of the texture
	 * @return The texture or null if not loaded
	 */
	public Texture getTexture(String name){
		return textures.get(name);
	}

	/**
	 * Informs TextureManager to free this texture.
	 *
	 * @param name name of the texture
	 */
	public void deleteTexture(String name){
		new DeleteTextureOperation(name).execute();
	}

    /**
     * Creaes font texture
     * @param font font which have not called load() yet
     */
    public void loadSTBFont(STBFont font) { new LoadSTBFontOperation(font).execute(); }

    /**
     * Deletes font texture
     * @param font font to release
     */
    public void deleteSTBFont(STBFont font) { new DeleteSTBFontOperation(font).execute(); }


	/**
	 * First use of this method also triggers constructor,
	 * assuming it has been called from GLFW thread.
	 * 
	 * @return singleton instance of TextureManager
	 */
	public static TextureManager getInstance(){
		if(instance != null){
			return instance;
		}else{
			instance = new TextureManager();
		}
		return instance;
	}
	
	abstract class Operation{
		abstract void execute();
	}
	
	class LoadBufferedImageTextureOperation extends Operation{
		
		private final String name;
		private BufferedImage img;

		private LoadBufferedImageTextureOperation(String name, BufferedImage img) {
			this.name = name;
			this.img = img;
		}
		
		//Not very efficient, but supports almost everything!
		void execute(){
			LOGGER.info ("Creating Texture: "+name);
			try {
				TextureFormat format = TextureFormat.getTextureFormat(img);
				
				ByteBuffer byteBuf;
				DataBuffer data = img.getRaster().getDataBuffer();

				if (data instanceof DataBufferByte) {
				    byte[] pixelData = ((DataBufferByte) data).getData();
				    byteBuf = ByteBuffer.allocateDirect(pixelData.length);
				    byteBuf.put(pixelData);
				    byteBuf.flip();
				}else if (data instanceof DataBufferUShort) {
				    short[] pixelData = ((DataBufferUShort) data).getData();
				    byteBuf = ByteBuffer.allocateDirect(pixelData.length * 2);
				    byteBuf.asShortBuffer().put(ShortBuffer.wrap(pixelData));
				    byteBuf.flip();
				}
				else if (data instanceof DataBufferShort) {
				    short[] pixelData = ((DataBufferShort) data).getData();
				    byteBuf = ByteBuffer.allocateDirect(pixelData.length * 2);
				    byteBuf.asShortBuffer().put(ShortBuffer.wrap(pixelData));
				    byteBuf.flip();
				}
				else if (data instanceof DataBufferInt) {
				    int[] pixelData = ((DataBufferInt) data).getData();
				    byteBuf = ByteBuffer.allocateDirect(pixelData.length * 4);
				    byteBuf.asIntBuffer().put(IntBuffer.wrap(pixelData));
				    byteBuf.flip();
				}else{
					LOGGER.severe("Unsupported format of data");
					return;
				}

				Texture newTexture = new Texture(name, img.getWidth(), img.getHeight(), byteBuf, format);
				LOGGER.info("Creating Texture "+name+" was successful. Adding to texture map.");
				textures.put(name, newTexture);
			} catch (Exception e) {
				LOGGER.info ("Exception when manipulating with Texture "+name+" data: "+e);
			}
		}
	}
	
	class loadSTBTextureOperation extends Operation{
		
		private final String name;
		private ByteBuffer imageBuffer;

		private loadSTBTextureOperation(String name, ByteBuffer imageBuffer) {
			this.name = name;
			this.imageBuffer = imageBuffer;
		}

		@Override
		void execute() {
			LOGGER.info ("Creating Texture: "+name);

			IntBuffer w = BufferUtils.createIntBuffer(1);
			IntBuffer h = BufferUtils.createIntBuffer(1);
			IntBuffer comp = BufferUtils.createIntBuffer(1);
			
			if ( stbi_info_from_memory(imageBuffer, w, h, comp) == 0 ){
				throw new RuntimeException("Failed to read image information: " + stbi_failure_reason());
			}

			ByteBuffer decodedImage = stbi_load_from_memory(imageBuffer, w, h, comp, 0);
			
			Texture newTexture = new Texture(name, w.get(0), h.get(0), decodedImage, (comp.get(0)==4)?TextureFormat.RGBA_B:TextureFormat.RGB_B);	
			textures.put(name, newTexture);
		}
	}

    class LoadSTBFontOperation extends Operation{

        private final STBFont target;

        public LoadSTBFontOperation(STBFont target){
            this.target = target;
        }

        void execute(){
            target.load();
        }
    }

	class DeleteTextureOperation extends Operation{

		private final String name;

		private DeleteTextureOperation(String name) {
			this.name = name;
		}

		void execute(){
			Texture toDelete = textures.get(name);

			if(toDelete != null){
				toDelete.lowerUsage();
				if(toDelete.getUsage() <= 0){
                    LOGGER.info ("Deleting Texture: "+name);
					toDelete.free();
					textures.remove(name);
				}
			}
		}
	}

    class DeleteSTBFontOperation extends Operation{

        private final STBFont target;

        public DeleteSTBFontOperation(STBFont target){
            this.target = target;
        }

        void execute(){
            target.delete();
        }
    }
	
}