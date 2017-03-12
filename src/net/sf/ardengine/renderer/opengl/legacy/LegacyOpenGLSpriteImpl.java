package net.sf.ardengine.renderer.opengl.legacy;

import javafx.scene.paint.Color;
import net.sf.ardengine.ASprite;
import net.sf.ardengine.Core;
import net.sf.ardengine.Sprite;
import net.sf.ardengine.renderer.opengl.lib.textures.Texture;
import net.sf.ardengine.renderer.opengl.lib.textures.TextureManager;
import net.sf.ardengine.renderer.ISpriteImpl;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import static org.lwjgl.opengl.GL11.*;

public class LegacyOpenGLSpriteImpl implements ISpriteImpl{

    protected static int genericSprites = 0;

    protected ASprite parentSprite;
    protected Texture spriteTexture;

    private final String textureName;

    public LegacyOpenGLSpriteImpl(InputStream is, ASprite parentSprite){
        this.textureName = "genericSprite"+genericSprites;
        genericSprites++;

        TextureManager.getInstance().loadTexture(textureName, is);
        this.spriteTexture = TextureManager.getInstance().getTexture(textureName);

        this.parentSprite = parentSprite;
    }

    public LegacyOpenGLSpriteImpl(BufferedImage buf, ASprite parentSprite){
        this.textureName = "genericSprite"+genericSprites;
        genericSprites++;

        TextureManager.getInstance().loadTexture(textureName, buf);
        this.spriteTexture = TextureManager.getInstance().getTexture(textureName);

        this.parentSprite = parentSprite;
    }

    public LegacyOpenGLSpriteImpl(String url, ASprite parentSprite){
        this.textureName = url;

        TextureManager.getInstance().loadTexture(url);
        this.spriteTexture = TextureManager.getInstance().getTexture(textureName);

        this.parentSprite = parentSprite;
    }

    public LegacyOpenGLSpriteImpl(Texture spriteTexture, Sprite parentSprite) {
        this.textureName = spriteTexture.getKey();
        this.spriteTexture = spriteTexture;
        spriteTexture.increaseUsage();
        this.parentSprite = parentSprite;
    }

    @Override
    public void draw() {
        if(spriteTexture == null){
            return; //TODO substitute, exception maybe?
        }

        glPushMatrix();

        spriteTexture.bind();

        float scale = parentSprite.getScale();
        float opacity = parentSprite.getOpacity();
        float rotated = parentSprite.getAngle();
        float x = (float)parentSprite.getX() - (parentSprite.isStatic()?0: Core.cameraX);
        float y = (float)parentSprite.getY() - (parentSprite.isStatic()?0: Core.cameraY);
        float height = (float)getHeight();
        float width = (float)getWidth();
        Color coloring = parentSprite.getColor();

        // translate to the right location and prepare to draw
        if (scale == 1) {
            //glTranslatef(net.sf.ardengine.Core.getCameraX() + x,
            //        net.sf.ardengine.Core.getCameraY() + y, 0);
            glTranslatef(x, y, 0);
        } else {
            //glTranslatef(net.sf.ardengine.Core.getCameraX() + x - width
            //        * (scale - 1) / 2, net.sf.ardengine.Core.getCameraY() + y
            //        - height * (scale - 1) / 2, 0);
            glTranslatef(x - width* (scale - 1) / 2, y - height * (scale - 1) / 2, 0);
        }

        glColor4d(coloring.getRed(), coloring.getGreen(), coloring.getBlue(), opacity);
        glScalef(scale, scale, 0);
        // do the rotation
        glTranslatef(width/2, height/2, 0);
        glRotatef(rotated, 0, 0, 1);
        glTranslatef(-width/2, -height/2, 0);
        render(width, height);
        // restore the model view matrix to prevent contamination
        glPopMatrix();
    }

    protected void render(float width, float height){
        glBegin(GL_QUADS);
        {
            glTexCoord2f(0, 0);
            glVertex2f(0, 0);
            glTexCoord2f(0, 1);
            glVertex2f(0, height);
            glTexCoord2f(1, 1);
            glVertex2f(width, height);
            glTexCoord2f(1, 0);
            glVertex2f(width, 0);
        }
        glEnd();
    }

    @Override
    public void update() {
        //draw() updates everything itself
    }

    @Override
    public void free() {
        spriteTexture.delete();
    }

    @Override
    public float getWidth() {
        if(spriteTexture == null) return -1;
        return spriteTexture.getWidth();
    }

    @Override
    public float getHeight() {
        if(spriteTexture == null) return -1;
        return spriteTexture.getHeight();
    }
}
