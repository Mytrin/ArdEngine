package net.sf.ardengine.renderer;

public interface ISpriteSheetImpl extends ISpriteImpl{

    /**
     * Uses texture from part of given image on index.
     * @param index Index of desired sprite in SpriteSheet.
     */
    public void setPartIndex(int index);

    /**
     * @return index of currently drawn sprite
     */
    public int getActualPartIndex();

    /**
     * @return Index of last sprite in SpriteSheet
     */
    public int getMaxIndex();
}