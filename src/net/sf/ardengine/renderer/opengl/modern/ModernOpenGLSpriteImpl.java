package net.sf.ardengine.renderer.opengl.modern;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.nio.FloatBuffer;

import javafx.scene.paint.Color;
import net.sf.ardengine.ASprite;
import net.sf.ardengine.renderer.opengl.lib.textures.Texture;
import net.sf.ardengine.renderer.opengl.lib.textures.TextureManager;
import net.sf.ardengine.renderer.ISpriteImpl;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;

public class ModernOpenGLSpriteImpl extends ModernOpenGLRenderable implements ISpriteImpl{

	protected static int genericSprites = 0;
	
	protected ASprite parentSprite;
	protected Texture spriteTexture;
	
	private final String textureName;

	private boolean requiresInit = true;

	protected final static float[] textureUVCoordinates = {
									0f,  0f,
									0f,  1f,
									1f,  0f,
									1f,  1f  
	};	
	protected final static FloatBuffer textureUVCoordinatesBuffer = BufferUtils.createFloatBuffer(textureUVCoordinates.length);
	static{
		textureUVCoordinatesBuffer.put(textureUVCoordinates);
		textureUVCoordinatesBuffer.flip();
	}
	
	public ModernOpenGLSpriteImpl(InputStream is, ASprite parentSprite){
		this.textureName = "genericSprite"+genericSprites;
        genericSprites++;

		TextureManager.getInstance().loadTexture(textureName, is);
        this.spriteTexture = TextureManager.getInstance().getTexture(textureName);
		
		this.parentSprite = parentSprite;
	}

	public ModernOpenGLSpriteImpl(BufferedImage buf, ASprite parentSprite){
		this.textureName = "genericSprite"+genericSprites;
        genericSprites++;

		TextureManager.getInstance().loadTexture(textureName, buf);
        this.spriteTexture = TextureManager.getInstance().getTexture(textureName);
		
		this.parentSprite = parentSprite;
	}
	
	public ModernOpenGLSpriteImpl(String url, ASprite parentSprite){
		this.textureName = url;

		TextureManager.getInstance().loadTexture(url);
        this.spriteTexture = TextureManager.getInstance().getTexture(textureName);

		this.parentSprite = parentSprite;
	}
	
	public ModernOpenGLSpriteImpl(Texture spriteTexture, ASprite parentSprite) {
		this.textureName = spriteTexture.getKey();
		this.spriteTexture = spriteTexture;
		spriteTexture.increaseUsage();
		this.parentSprite = parentSprite;
	}

	@Override
	public void draw() {
		if(spriteTexture == null){
			return; //TODO substitute, exception?
		}

        if(requiresInit){
            init(2);
            requiresInit = false;
        }

        render(ModernOpenGLRenderer.SPRITE_SHADER);
	}

    @Override
    public void update() {
        //nothing special + ModernOpenGLRenderable updates everything
    }

    @Override
    public void free() {
        freeBuffers();
        spriteTexture.delete();
    }

    @Override
    protected void customBufferInit() {
            float hWidth = (float)getRenderWidth()/2f;
            float hHeight = (float)getRenderHeight()/2f;

            float[] vertexPositions = new float[]{
                -hWidth,  -hHeight,    0f, 1f,
                -hWidth,   hHeight,    0f, 1f,
                hWidth,  -hHeight,    0f, 1f,
                hWidth,   hHeight,    0f, 1f
            };


            FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(vertexPositions.length);
            vertexPositionsBuffer.put(vertexPositions);
            vertexPositionsBuffer.flip();

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(0));
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexPositionsBuffer, GL15.GL_STATIC_DRAW);

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(1));
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textureUVCoordinatesBuffer, GL15.GL_STATIC_DRAW);
    }

    @Override
    protected void customBind() {
        GL20.glEnableVertexAttribArray(0);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(0));
        GL20.glVertexAttribPointer(
                0,                 	//must match the layout in the shader!
                4,                 	// size
                GL11.GL_FLOAT,     	// type
                false,           		// normalized?
                0,                  	// stride
                0            			// array buffer offset
        );

        GL20.glEnableVertexAttribArray(1);
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(1));
        GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0);

        spriteTexture.bind();
    }

    @Override
    protected float getRenderAngle() {
        return parentSprite.getAngle();
    }

    @Override
    protected float getRenderOpacity() {
        return parentSprite.getOpacity();
    }

    @Override
    protected float getRenderScale() {
        return parentSprite.getScale();
    }

    @Override
    protected Color getRenderColor() {
        return parentSprite.getColor();
    }

    @Override
    protected float getRenderWidth() {
        return parentSprite.getWidth();
    }

    @Override
    protected float getRenderHeight() {
        return parentSprite.getHeight();
    }

    @Override
    protected float getRenderX() {
        return parentSprite.getX();
    }

    @Override
    protected float getRenderY() {
        return parentSprite.getY();
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

    @Override
    protected boolean isRenderStatic() {
        return parentSprite.isStatic();
    }
}