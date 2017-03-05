package net.sf.ardengine.io;


import net.sf.ardengine.Core;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class making easier handling XML files and loading basic library configuration.
 *
 * @author Martin Lepeška
 */
public final class Config {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    /** Indicator that some configuration was already loaded */
    private static boolean configLoaded = false;
    /** Loaded values */
    private static final Map<String, String> CONFIG = new HashMap<>();
    /** Singleton */
    private Config(){}

    /**
     * Loads and sets new configuration from default config.
     */
    public synchronized static void loadDefaultConfig(){
        try{
            loadConfig(Core.class.getClassLoader().getResourceAsStream("net/sf/ardengine/res/config.xml"));
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Default config file not found!", e);
        }
    }

    /**
     * Loads and sets new configuration.
     * @param source config file
     */
    public synchronized static final void loadConfig(File source){
        try{
            loadConfig(new FileInputStream(source));
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Config file not found!", e);
        }
    }

    /**
     * Loads and sets new configuration.
     * @param is Stream to config file
     *
     * @throws java.lang.Exception
     */
    public synchronized static final void loadConfig(InputStream is){
        try{
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);

            NodeList configValues = doc.getDocumentElement().getElementsByTagName("item");

            for (int i = 0; i < configValues.getLength(); i++) {
                Node tag = configValues.item(i);
                CONFIG.put(tag.getAttributes().getNamedItem("name").getTextContent(), tag.getTextContent());
            }

            configLoaded = true;
        }catch(Exception e){
            LOGGER.log(Level.SEVERE, "Config not loaded!", e);
        }
    }

    /**
     * @return true, if config was loaded
     */
    public synchronized static boolean isConfigLoaded() {
        return configLoaded;
    }

    /**
     * @param itemName name of tag
     * @param value new value
     */
    public synchronized static void set(String itemName, String value){
        CONFIG.put(itemName, value);
    }

    /**
     * @param itemName name of tag
     * @param defaultValue desired value, in case tag was missing
     * @return its value or null
     */
    public synchronized static String getString(String itemName, String defaultValue){
        String value = CONFIG.get(itemName);
        if(value != null) return value;
        return defaultValue;
    }

    /**
     * @param itemName name of tag
     * @param defaultValue desired value, in case tag was missing
     * @return its value or null
     */
    public synchronized static int getString(String itemName, int defaultValue){
        String value = CONFIG.get(itemName);
        if(value == null) return defaultValue;
        try{
            return Integer.parseInt(value);
        }catch (Exception e){
            return defaultValue;
        }
    }

    /**
     * @param itemName name of tag
     * @param defaultValue desired value, in case tag was missing
     * @return its value or null
     */
    public synchronized static float getFloat(String itemName, float defaultValue){
        String value = CONFIG.get(itemName);
        if(value == null) return defaultValue;
        try{
            return Float.parseFloat(value);
        }catch (Exception e){
            return defaultValue;
        }
    }

    /**
     * @param itemName name of tag
     * @param defaultValue desired value, in case tag was missing
     * @return its value or null
     */
    public synchronized static Quality getQuality(String itemName, Quality defaultValue){
        String value = CONFIG.get(itemName);
        if(value != null) {
            return Quality.valueOf(value);
        }
        return defaultValue;
    }

    public enum Quality {
        FAST,
        NORMAL,
        BEST
    }

}