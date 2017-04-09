package net.sf.ardengine.renderer.util;

import net.sf.ardengine.ASprite;
import net.sf.ardengine.renderer.opengl.lib.textures.Texture;

/**
 * Common calculation for SpriteSheet implementations
 */
public class SpriteSheetCalc {

    public final float partWidth;
    public final float partHeight;

    public final int rows;
    public final int cols;
    public final float rowUV;
    public final float colUV;

    public final int maxIndex;

    public SpriteSheetCalc(ASprite parentSprite, Texture sheet) {
        this(parentSprite,sheet.getWidth(), sheet.getHeight());
    }

    public SpriteSheetCalc(ASprite parentSprite, float sheetWidth, float sheetHeight) {
        partWidth = parentSprite.getWidth();
        partHeight = parentSprite.getHeight();
        cols = Math.max((int)Math.ceil(sheetWidth/partWidth), 1);
        rows = Math.max((int)Math.ceil(sheetHeight/partHeight), 1);

        rowUV = 1f/rows;
        colUV = 1f/cols;

        maxIndex = rows * cols -1;
    }

    public float[] createPartUV(int index){
        int row = (int)Math.floor(index/cols);
        int col = index%cols;

        float startWidth = col*colUV;
        float startHeight = row*rowUV;
        float endWidth = startWidth+colUV;
        float endHeight = startHeight+rowUV;

        return new float[]{
                startWidth,     startHeight,
                startWidth,     endHeight,
                endWidth,       startHeight,
                endWidth,       endHeight
        };
    }

    public float[] getPartLegacyUV(int index){
        int row = (int)Math.floor(index/cols);
        int col = index%cols;

        float startWidth = col*colUV;
        float startHeight = row*rowUV;
        float endWidth = startWidth+colUV;
        float endHeight = startHeight+rowUV;

        return new float[]{
                startWidth, startHeight,
                endWidth,   endHeight
        };
    }

    public int validateIndex(int index){
        if(index > maxIndex){
            return 0;
        }else if(index < 0){
            return maxIndex;
        }
        return index;
    }
}
