package net.sf.ardengine.renderer.javaFXRenderer;

import javafx.geometry.Rectangle2D;
import net.sf.ardengine.SpriteSheet;
import net.sf.ardengine.renderer.ISpriteSheetImpl;
import net.sf.ardengine.renderer.util.SpriteSheetCalc;

import java.awt.image.BufferedImage;
import java.io.InputStream;

public class JavaFXSpriteSheetImpl extends JavaFXSpriteImpl implements ISpriteSheetImpl {

    private SpriteSheetCalc sheetCalc;

    private int currentIndex = 0;

    public JavaFXSpriteSheetImpl(InputStream is, SpriteSheet parentSprite){
        super(is, parentSprite);
        init();
    }

    public JavaFXSpriteSheetImpl(String url, SpriteSheet parentSprite){
        super(url, parentSprite);
        init();
    }

    public JavaFXSpriteSheetImpl(BufferedImage buf, SpriteSheet parentSprite){
        super(buf, parentSprite);
        init();
    }

    private void init(){
        sheetCalc = new SpriteSheetCalc(parentSprite, (float)getImage().getWidth(), (float)getImage().getHeight());
        setPartIndex(currentIndex);
    }

    @Override
    public float getHeight() {
        return sheetCalc.partHeight;
    }

    @Override
    public float getWidth() {
        return sheetCalc.partWidth;
    }

    @Override
    public void setPartIndex(int index) {
        if(sheetCalc==null){
            currentIndex = index;
            return;
        }

        currentIndex = sheetCalc.validateIndex(index);

        float[] newCoords = sheetCalc.getPartLegacyUV(currentIndex);

        float startWidth = newCoords[0] * (float)getImage().getWidth();
        float startHeight = newCoords[1] * (float)getImage().getHeight();
        float endWidth = newCoords[2] * (float)getImage().getWidth();
        float endHeight = newCoords[3] * (float)getImage().getHeight();

        Rectangle2D viewport = new Rectangle2D(startWidth, startHeight, endWidth, endHeight);
        setViewport(viewport);
    }

    @Override
    public int getActualPartIndex() {
        return currentIndex;
    }

    @Override
    public int getMaxIndex() {
        return sheetCalc.maxIndex;
    }
}