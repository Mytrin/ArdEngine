package net.sf.ardengine;

import java.io.InputStream;
import java.util.HashMap;
import java.util.logging.Logger;

import net.sf.ardengine.text.IFont;

public class FontManager {
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /** Name of default font used by renderer, when it could not use font loaded by user*/
    public static final String DEFAULT_KEY = "default";

	/**Loaded fonts*/
	private static HashMap<String, IFont> loadedFonts = new HashMap<>();

	 /**
    * Loads given font to manager
    * @param fontName name of font from java to load 
    * @param nameID  String, with which will be font accessed 
    * @param size  Size of font
    * @param specialCharacters language dependent characters to load
    */
   public static void loadFont(String fontName, String nameID, int size, char[] specialCharacters){
      IFont newFont = Core.renderer.loadFont(fontName, size, specialCharacters);
      if(newFont != null){
      	loadedFonts.put(nameID, newFont);
        LOGGER.info("Loading font "+fontName+" with ID "+nameID);
      }else{
        LOGGER.info("Failed to load font "+fontName+" with ID "+nameID);
      }
   }
   
	 /**
    * Loads given font to manager
    * @param font Font source
    * @param nameID  String, with which will be font accessed 
    * @param size  Size of font
    * @param specialCharacters language dependent characters to load
    */
   public static void loadFont(InputStream font, String nameID, int size, char[] specialCharacters){
   	IFont newFont = Core.renderer.loadFont(font, size, specialCharacters);
      if(newFont != null){
      	loadedFonts.put(nameID, newFont);
          LOGGER.info("Loading font with ID "+nameID);
      }else{
          LOGGER.info("Failed to load font with ID "+nameID);
      }
   }
   
   /**
    * Retrieves font from map of loaded fonts
    * @param nameID font identifier
    * @return Font, or null if nothing found
    */
   public static IFont getFont(String nameID){
   	return loadedFonts.get(nameID);
   }
   
   /**
    * Removes font from map of loaded fonts
    * @param nameID font identifier
    */
   public static void deleteFont(String nameID){
       IFont font = loadedFonts.get(nameID);
       if(font!= null){
           font.free();
       }
       loadedFonts.remove(nameID);
   }
   
   /**
    * Clears all fonts from memory.
    */
   public static void clear(){
   	    for(String key : loadedFonts.keySet()){
            deleteFont(key);
        }
   }
}