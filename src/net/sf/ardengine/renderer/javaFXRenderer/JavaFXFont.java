package net.sf.ardengine.renderer.javaFXRenderer;

import java.io.InputStream;
import javafx.scene.text.Font;

import net.sf.ardengine.text.IFont;

public class JavaFXFont implements IFont{

	 protected final Font fxFont;
	
	 /**
    * @param fontName name of font from java to load 
    * @param size  Size of font
    */
   public JavaFXFont(String fontName, int size){
       fxFont = new Font(fontName, size);
   }
   
	 /**
    * @param font Font source
    * @param size  Size of font
    */
   public JavaFXFont(InputStream font, int size){
       fxFont = Font.loadFont(font, size);
   }

   public Font getFxFont() {
		return fxFont;
	}

    @Override
    public int getFontHeight() {
        return (int)Math.ceil(fxFont.getSize());
    }

    @Override
    public void free() {}

}
