package net.sf.ardengine.renderer.opengl.lib.text;

import net.sf.ardengine.Core;
import net.sf.ardengine.io.Config;
import net.sf.ardengine.io.Util;
import net.sf.ardengine.renderer.opengl.OpenGLRenderer;
import net.sf.ardengine.renderer.opengl.lib.textures.TextureManager;
import net.sf.ardengine.text.IFont;
import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Logger;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBTruetype.*;
import static org.lwjgl.stb.STBTruetype.stbtt_PackFontRange;

/**
 * OpenGL font implementation using STB TrueType library.
 *
 * 11.9.2016 Added synchronized, in suspicion text might be rendered, while font is still loading.
 */
public class STBFont implements IFont {

    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);

    public static final double PT_TO_PX = 4.0/3.0;

    private final int verticalOversampling;
    private final int horizontalOversampling;

    private int firstChar = 32;
    private int lastChar = 128;

    private final int fontHeight;

    private String path;
    private InputStream source;

    private int fontTextureID = 0;

    //private STBTTBakedChar.Buffer charData;
    private STBTTPackedchar.Buffer charData;
    private HashMap<Character, Character> specialCharMap= new HashMap<>();

    private int bitmapSize = -1;

    private char[] specialCharacters;

    /**
     * @param path path to ttf
     * @param fontHeight size of font in pts
     * @param specialCharacters language dependent characters to load
     */
    public STBFont(String path, int fontHeight, char[] specialCharacters){
        this(fontHeight, specialCharacters);
        this.path = path;
    }

    /**
     * @param source stream to ttf
     * @param fontHeight size of font in pts
     * @param specialCharacters language dependent characters to load
     */
    public STBFont(InputStream source, int fontHeight, char[] specialCharacters){
        this(fontHeight, specialCharacters);
        this.source = source;
    }

    /**
     * @param fontHeight size of font in pts
     * @param specialCharacters language dependent characters to load
     */
    private STBFont(int fontHeight, char[] specialCharacters){
        this.fontHeight = (int)Math.ceil(fontHeight*PT_TO_PX);
        if(specialCharacters != null){
            this.specialCharacters = specialCharacters;
        }else{
            this.specialCharacters = new char[]{};
        }

        Config.Quality configQuality = Config.getQuality("text-quality", Config.Quality.NORMAL);
        int textQuality = 2;

        if(configQuality != Config.Quality.NORMAL){
            if(configQuality == Config.Quality.BEST){
                textQuality = 3;
            }else{
                textQuality = 1;
            }
        }

        this.verticalOversampling = textQuality;
        this.horizontalOversampling = textQuality;
    }

    /**
     * Automatically called by renderer
     * @return true, if font was successfully loaded to texture.
     */
    public synchronized boolean load() {
        fontTextureID = glGenTextures();

        //charData = STBTTBakedChar.malloc(lastChar-firstChar);
        int charCount = (lastChar-firstChar)+specialCharacters.length;

        charData = STBTTPackedchar.malloc(charCount+firstChar*2);

        try {
            ByteBuffer ttf;
            if(path != null){
                ttf = Util.sourceToByteBuffer(path, fontHeight*fontHeight * charCount*2);
            }else{
                ttf = Util.streamToByteBuffer(source, fontHeight*fontHeight * charCount*2);
            }

            bitmapSize= (int)Math.ceil(Math.sqrt(charCount))*fontHeight;
            bitmapSize*= horizontalOversampling * verticalOversampling;

            if(bitmapSize > glGetInteger(GL_MAX_TEXTURE_SIZE)){
                throw new RuntimeException("Loading font failed, texture would be too large!");
            }
            LOGGER.info("Creating font texture "+bitmapSize+"x"+bitmapSize);

            ByteBuffer bitmap = BufferUtils.createByteBuffer(bitmapSize * bitmapSize);

            //stbtt_BakeFontBitmap(ttf, fontHeight, bitmap, bitmapSize, bitmapSize, firstChar, charData);

            // OVERSAMPLING METHOD
            STBTTPackContext pc = STBTTPackContext.malloc();
            stbtt_PackBegin(pc, bitmap, bitmapSize, bitmapSize, 0, 1, null);
            charData.limit(lastChar);
            charData.position(firstChar);
            stbtt_PackSetOversampling(pc, 2, 2);
            stbtt_PackFontRange(pc, ttf, 0, fontHeight, firstChar, charData);

            loadSpecialCharacters(pc, ttf);

            charData.clear();
            stbtt_PackEnd(pc);
            // OVERSAMPLING METHOD

            glBindTexture(GL_TEXTURE_2D, fontTextureID);

            //Ugly workaround to get this working in newer versions
            int colorStore = (((OpenGLRenderer) Core.renderer).getMajorVersion() > 1)?GL_RED:GL_ALPHA;

            glTexImage2D(GL_TEXTURE_2D, 0, colorStore, bitmapSize, bitmapSize, 0, colorStore, GL_UNSIGNED_BYTE, bitmap);

            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
            glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
            return true;
        } catch (IOException e) {
            LOGGER.severe(e.getMessage());
        }
        return false;
    }


    /**
     * Invokes stbtt_GetBakedQuad
     *
     * @param x buffer with vertical offset
     * @param y buffer with horizontal offset
     * @param quad quad to fill with info
     * @param ch character, about which the info should be
     */
    public synchronized void activateChar(FloatBuffer x, FloatBuffer y, STBTTAlignedQuad quad, char ch){
        //stbtt_GetBakedQuad(font.getCharData(), font.getBitmapSize(), font.getBitmapSize(), wordArray[i].charAt(0) - 32, x, y, quad, true);

        if(ch >= firstChar && ch <= lastChar){
            stbtt_GetPackedQuad(charData, bitmapSize, bitmapSize, ch, x, y, quad, true);
        }else{
            Character address = specialCharMap.get(ch);

            if(address != null){
                stbtt_GetPackedQuad(charData, bitmapSize, bitmapSize, address, x, y, quad, true);
            }else{
                //?
                stbtt_GetPackedQuad(charData, bitmapSize, bitmapSize, (char)63, x, y, quad, true);
            }
        }
    }

    private void loadSpecialCharacters(STBTTPackContext pc, ByteBuffer ttf){
        if(specialCharacters!= null){
            Arrays.sort(specialCharacters);
        }

        //find sequences
        int index=0;
        while(index < specialCharacters.length){

            int start = index;
            int end = index;

            while(end+1 < specialCharacters.length && (specialCharacters[end]+1)==(specialCharacters[end+1])){
                end++;
            }
            index = end+1;

            //System.out.println("SEQUENCE "+specialCharacters[start]+"["+(int)specialCharacters[start]+"] -> "+specialCharacters[end]+"["+(int)specialCharacters[end]+"]");

            charData.limit(lastChar+end+1);
            charData.position(lastChar+start);
            for(int i=start; i <=end; i++){
                specialCharMap.put(specialCharacters[i], (char)(lastChar+i));
                //System.out.println(specialCharacters[i]+" MASKED AS "+(lastChar+i));
            }
            stbtt_PackFontRange(pc, ttf, 0, fontHeight, specialCharacters[start], charData);
        }

    }

    /**
     * Automatically called by renderer
     * Frees font texture
     */
    public synchronized void delete(){
        charData.free();
        glDeleteTextures(fontTextureID);
    }

    @Override
    public synchronized void free() {
        TextureManager.getInstance().deleteSTBFont(this);
    }

    /**
     * @return Bitmap size for stb purposes
     */
    public int getBitmapSize() {
        return bitmapSize;
    }

    public void bind(){
        glBindTexture(GL_TEXTURE_2D, fontTextureID);
    }

    /**
     * @return STB struct with characters
     */
    public synchronized STBTTPackedchar.Buffer getCharData() {
        return charData;
    }

    @Override
    public int getFontHeight() {
        return fontHeight;
    }
}