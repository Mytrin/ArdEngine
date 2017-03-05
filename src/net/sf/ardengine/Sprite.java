package net.sf.ardengine;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;

/**
 * Simple sprite class, represents image
 */
public class Sprite extends ASprite{
    /**
     * @param is Stream to image file
     */
    public Sprite(InputStream is){
        implementation = Core.renderer.createSpriteImplementation(is, this);
    }

    /**
     * @param url Path to image file
     */
    public Sprite(String url){
        if(new File(url).exists()){
            implementation = Core.renderer.createSpriteImplementation(url, this);
        }else{
            implementation = null;
            System.err.println(url+" not found!");
        }
    }

    /**
     * @param buf Image data
     */
    public Sprite(BufferedImage buf){
        implementation = Core.renderer.createSpriteImplementation(buf, this);
    }
}