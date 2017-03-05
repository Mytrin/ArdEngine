package net.sf.ardengine;

import net.sf.ardengine.renderer.ISpriteSheetImpl;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class SpriteSheet extends ASprite{

    /**Width of one sprite sheet image*/
    private final int partWidth;
    /**Height of one sprite sheet image*/
    private final int partHeight;

    /**
     * @param is Stream to image file
     * @param partWidth Width of one sprite sheet image
     * @param partHeight Height of one sprite sheet image
     */
    public SpriteSheet(InputStream is, int partWidth, int partHeight){
        this.partWidth = partWidth;
        this.partHeight = partHeight;
        implementation = Core.renderer.createSpriteSheetImplementation(is, this);
    }

    /**
     * @param url Path to image file
     * @param partWidth Width of one sprite sheet image
     * @param partHeight Height of one sprite sheet image
     */
    public SpriteSheet(String url, int partWidth, int partHeight){
        this.partWidth = partWidth;
        this.partHeight = partHeight;
        implementation = Core.renderer.createSpriteSheetImplementation(url, this);
    }

    /**
     * @param buf Image data
     * @param partWidth Width of one sprite sheet image
     * @param partHeight Height of one sprite sheet image
     */
    public SpriteSheet(BufferedImage buf, int partWidth, int partHeight){
        this.partWidth = partWidth;
        this.partHeight = partHeight;
        implementation = Core.renderer.createSpriteSheetImplementation(buf, this);
    }

    @Override
    public float getWidth() {
        return partWidth;
    }

    @Override
    public float getHeight() {
        return partHeight;
    }

    /**
     * Uses texture from part of given image on index.
     * @param index Index of desired sprite in SpriteSheet.
     */
    public void setPartIndex(int index){
        ((ISpriteSheetImpl)implementation).setPartIndex(index);
    }

    /**
     * @return index of currently drawn sprite
     */
    public int getPartIndex() {
        return ((ISpriteSheetImpl)implementation).getActualPartIndex();
    }

    /**
     * @return Index of last sprite in SpriteSheet
     */
    public int getMaxIndex() {
        return ((ISpriteSheetImpl)implementation).getMaxIndex();
    }

}