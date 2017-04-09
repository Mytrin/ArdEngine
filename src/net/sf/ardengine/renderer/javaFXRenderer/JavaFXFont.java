package net.sf.ardengine.renderer.javaFXRenderer;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

import javafx.scene.text.Font;

import net.sf.ardengine.text.IFont;

public class JavaFXFont implements IFont{

	 protected final Font fxFont;
	
	 /**
    * @param fontName name of font from java to load 
    * @param size  Size of font
    */
   public JavaFXFont(String fontName, int size){
       Font loadedFont;
       try{
           File source = new File(fontName);
           FileInputStream sourceStream =new FileInputStream(source);

           loadedFont = Font.loadFont(sourceStream, size);
           System.out.println(loadedFont.getName());
       }catch(Exception e){
           Logger.getLogger(JavaFXFont.class.getName()).severe("Failed when loading font "+fontName+": "+e);
           loadedFont = new Font(size);
       }
       fxFont = loadedFont;
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
       if(fxFont == null) return -1;
        return (int)Math.ceil(fxFont.getSize());
    }

    @Override
    public void free() {}

}
