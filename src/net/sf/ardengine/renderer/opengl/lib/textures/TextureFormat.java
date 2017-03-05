package net.sf.ardengine.renderer.opengl.lib.textures;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;


/**
 * 
 * EXPERIMENTAL!
 * UNTESTED!
 *
 * Some "rare" formats will be rendered incorrectly!
 * Formats were created according to
 * https://docs.oracle.com/javase/7/docs/api/java/awt/image/BufferedImage.html
 * However, only the common ones are supported. The rest is either experimental or fallback. 
 */
public enum TextureFormat {
	RGB_I(GL11.GL_RGB, GL11.GL_RGB, GL11.GL_UNSIGNED_INT),
	RGBA_I(GL11.GL_RGBA, GL11.GL_RGBA, GL11.GL_UNSIGNED_INT),
	BGR_I(GL11.GL_RGB, GL12.GL_BGR, GL11.GL_UNSIGNED_INT),
	BGRA_I_REV(GL11.GL_RGBA, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV),
	
	RGB_B(GL11.GL_RGB, GL11.GL_RGB, GL11.GL_UNSIGNED_BYTE),
	RGBA_B(GL11.GL_RGBA, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE),
	BGR_B(GL11.GL_RGB, GL12.GL_BGR, GL11.GL_UNSIGNED_BYTE),
	BGRA_B(GL11.GL_RGBA, GL12.GL_BGRA, GL11.GL_UNSIGNED_BYTE),
	
	GRAY_B(GL11.GL_RED, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE),
	GRAY_US(GL11.GL_RED, GL11.GL_RED, GL11.GL_UNSIGNED_SHORT),
	
	RGB_565(GL11.GL_RGB, GL11.GL_RGB, GL12.GL_UNSIGNED_SHORT_5_6_5),

	GRAY_BIN(GL11.GL_RED, GL11.GL_RED, GL11.GL_UNSIGNED_BYTE),

	ALPHA_B(GL11.GL_ALPHA, GL11.GL_ALPHA, GL11.GL_UNSIGNED_BYTE)
	;
	/**Number of components*/
	private final int internalFormat;
	/**Data type*/
	private final int format;
	/**Data layout*/
	private final int type;
	
	TextureFormat(int internalFormat, int format, int type){
		this.internalFormat = internalFormat;
		this.format = format;
		this.type = type;
	}
	
	public int getInternalFormat() {
		return internalFormat;
	}
	
	public int getFormat() {
		return format;
	}
	
	public int getType() {
		return type;
	}
	
	//TODO test
	/**
	 * This method tries to assign specific texture format to given image.
	 * In some cases it also converts image data for appropriate format.
	 * Not all types of images are supported! 
	 * 
	 * TESTED: TYPE_4BYTE_ABGR
	 * 			TYPE_3BYTE_BGR
	 * 
	 * @param buf image requiring texture format 
	 * @return texture format for given image
	 */
	public static TextureFormat getTextureFormat(BufferedImage buf){
				
		switch(buf.getType()){
			case BufferedImage.TYPE_INT_RGB: return RGB_I;
			case BufferedImage.TYPE_INT_ARGB: return BGRA_I_REV;
			//The color data in this image is considered to be premultiplied with alpha.
			case BufferedImage.TYPE_INT_ARGB_PRE: return BGRA_I_REV;
			case BufferedImage.TYPE_INT_BGR: return BGR_I;
			case BufferedImage.TYPE_3BYTE_BGR: return BGR_B;
			case BufferedImage.TYPE_4BYTE_ABGR: swapComponentOrderByte(4, buf); 
															return RGBA_B;
			//The color data in this image is considered to be premultiplied with alpha.
			case BufferedImage.TYPE_4BYTE_ABGR_PRE: swapComponentOrderByte(4, buf); 
																return RGBA_B;
			case BufferedImage.TYPE_BYTE_GRAY: return GRAY_B;
			//needs custom shader, or manipulated data
			case BufferedImage.TYPE_BYTE_BINARY: return GRAY_BIN;
			//needs custom shader, or manipulated data
			case BufferedImage.TYPE_BYTE_INDEXED: return RGB_B;
			case BufferedImage.TYPE_USHORT_GRAY: return GRAY_US;
			case BufferedImage.TYPE_USHORT_565_RGB: return RGB_565;
			case BufferedImage.TYPE_USHORT_555_RGB: return RGB_565;
			case BufferedImage.TYPE_CUSTOM:  return RGB_B;
		}
		
		return RGB_B;
	}
	
	private static void swapComponentOrderByte(int componentCount, BufferedImage buf){
		DataBufferByte data = (DataBufferByte)buf.getRaster().getDataBuffer();
		int banks = data.getNumBanks();
		int bankSize = data.getSize()/banks;

		for(int bank =0; bank < banks; bank++){
			for(int i=0; i<bankSize; i+=componentCount){
				for(int j=0; j<Math.floor(componentCount/2); j++){
					int fLeft = data.getElem(bank, i+j);
					data.setElem(bank, i+j, data.getElem(bank, i+componentCount-j-1));
					data.setElem(bank, i+componentCount-j-1, fLeft);
				}
			}
		}
		
	}
}
