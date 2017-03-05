package net.sf.ardengine.renderer.opengl.modern;

import net.sf.ardengine.SpriteSheet;
import net.sf.ardengine.renderer.ISpriteSheetImpl;
import net.sf.ardengine.renderer.util.SpriteSheetCalc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.FloatBuffer;

public class ModernOpenGLSpriteSheetImpl extends ModernOpenGLSpriteImpl implements ISpriteSheetImpl {

    private int currentIndex = 0;

    private SpriteSheetCalc sheetCalc;

    private FloatBuffer currentUV= BufferUtils.createFloatBuffer(8);
    private boolean rebindUV = true;

    public ModernOpenGLSpriteSheetImpl(InputStream is, SpriteSheet parentSprite){
        super(is, parentSprite);
    }

    public ModernOpenGLSpriteSheetImpl(BufferedImage buf, SpriteSheet parentSprite){
        super(buf, parentSprite);
    }

    public ModernOpenGLSpriteSheetImpl(String url, SpriteSheet parentSprite){
        super(url, parentSprite);
    }

    @Override
    protected void init(int customBuffers) {
        //Can only be used after texture is loaded
        sheetCalc = new SpriteSheetCalc(parentSprite, spriteTexture);

        super.init(customBuffers);

        setPartIndex(currentIndex);
    }

    @Override
    public void draw() {
        if(rebindUV && sheetCalc!=null) rebindUVBuffer(currentUV);

        super.draw();
    }

    @Override
    public void setPartIndex(int index){

        if(sheetCalc==null){
            currentIndex = index;
            return;
        }

        currentIndex = sheetCalc.validateIndex(index);

        currentUV.clear();
        currentUV= BufferUtils.createFloatBuffer(8);
        currentUV.put(sheetCalc.createPartUV(currentIndex));
        currentUV.flip();
        rebindUV = true;
    }

    private void rebindUVBuffer(FloatBuffer uvBuffer){
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(1));
        GL15.glBufferData(GL15.GL_ARRAY_BUFFER, uvBuffer, GL15.GL_STATIC_DRAW);
        rebindUV = false;
    }

    @Override
    public int getMaxIndex() {
        if(sheetCalc == null) return -1;
        return sheetCalc.maxIndex;
    }

    @Override
    public int getActualPartIndex() {
        return currentIndex;
    }

    @Override
    protected float getRenderWidth() {
        return sheetCalc.partWidth;
    }

    @Override
    protected float getRenderHeight() {
        return sheetCalc.partHeight;
    }

    @Override
    public float getHeight() {
        return sheetCalc.partHeight;
    }

    @Override
    public float getWidth() {
        return sheetCalc.partWidth;
    }
}