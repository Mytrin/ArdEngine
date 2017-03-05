package net.sf.ardengine.renderer.opengl.legacy;


import net.sf.ardengine.SpriteSheet;
import net.sf.ardengine.renderer.ISpriteSheetImpl;
import net.sf.ardengine.renderer.util.SpriteSheetCalc;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

public class LegacyOpenGLSpriteSheetImpl extends LegacyOpenGLSpriteImpl implements ISpriteSheetImpl {

    private int currentIndex = 0;

    float startWidth = 0;
    float startHeight = 0;
    float endWidth = 1;
    float endHeight = 1;

    private SpriteSheetCalc sheetCalc;

    public LegacyOpenGLSpriteSheetImpl(InputStream is, SpriteSheet parentSprite){
        super(is, parentSprite);
    }

    public LegacyOpenGLSpriteSheetImpl(BufferedImage buf, SpriteSheet parentSprite){
        super(buf, parentSprite);
    }

    public LegacyOpenGLSpriteSheetImpl(String url, SpriteSheet parentSprite){
        super(url, parentSprite);
    }

    private void init(){
        sheetCalc = new SpriteSheetCalc(parentSprite, spriteTexture);
        setPartIndex(currentIndex);

    }

    protected void render(float width, float height){
        if(sheetCalc== null){
            init();
        }

        glBegin(GL_QUADS);
        {
            glTexCoord2f(startWidth, startHeight);
            glVertex2f(0, 0);
            glTexCoord2f(startWidth, endHeight);
            glVertex2f(0, height);
            glTexCoord2f(endWidth, endHeight);
            glVertex2f(width, height);
            glTexCoord2f(endWidth, startHeight);
            glVertex2f(width, 0);
        }
        glEnd();
    }

    @Override
    public float getHeight() {
        if(sheetCalc != null) return sheetCalc.partHeight;
        return parentSprite.getHeight();
    }

    @Override
    public float getWidth() {
        if(sheetCalc != null) return sheetCalc.partWidth;
        return parentSprite.getWidth();
    }

    @Override
    public void setPartIndex(int index) {
        if(sheetCalc==null){
            currentIndex = index;
            return;
        }

        currentIndex = sheetCalc.validateIndex(index);

        float[] newCoords = sheetCalc.getPartLegacyUV(currentIndex);

        startWidth = newCoords[0];
        startHeight = newCoords[1];
        endWidth = newCoords[2];
        endHeight = newCoords[3];
    }

    @Override
    public int getActualPartIndex() {
        return currentIndex;
    }

    @Override
    public int getMaxIndex() {
        if(sheetCalc == null) return -1;
        return sheetCalc.maxIndex;
    }
}