package net.sf.ardengine.renderer.opengl.modern;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

import javafx.scene.paint.Color;
import net.sf.ardengine.Group;
import net.sf.ardengine.Node;
import net.sf.ardengine.SpriteSheet;
import net.sf.ardengine.collisions.CollisionShape;
import net.sf.ardengine.io.Config;
import net.sf.ardengine.renderer.ISpriteSheetImpl;
import net.sf.ardengine.renderer.opengl.modern.shapes.ModernOpenGLCircleImpl;
import net.sf.ardengine.renderer.opengl.modern.shapes.ModernOpenGLLineImpl;
import net.sf.ardengine.renderer.opengl.modern.shapes.ModernOpenGLPolygonImpl;
import net.sf.ardengine.renderer.opengl.modern.shapes.ModernOpenGLRectangleImpl;
import net.sf.ardengine.renderer.opengl.modern.util.ModernCollisionRenderer;
import net.sf.ardengine.renderer.util.IRenderableCollision;
import net.sf.ardengine.shapes.*;
import net.sf.ardengine.text.IFont;
import net.sf.ardengine.text.ITextImpl;
import net.sf.ardengine.text.Text;

import net.sf.ardengine.Sprite;
import net.sf.ardengine.renderer.opengl.OpenGLRenderer;
import net.sf.ardengine.renderer.opengl.lib.shader.Matrix;
import net.sf.ardengine.renderer.opengl.lib.shader.Shader;
import net.sf.ardengine.renderer.opengl.lib.shader.Uniform;
import net.sf.ardengine.renderer.ISpriteImpl;

import static org.lwjgl.opengl.GL11.*;

public class ModernOpenGLRenderer extends OpenGLRenderer{
	
	private static final int GL_MAJOR_VERSION = 3;
	private static final int GL_MINOR_VERSION = 3;
	
	public static String SPRITE_SHADER_NAME = "net/sf/ardengine/res/shaders/image";
	public static Shader SPRITE_SHADER;
    public static String TEXT_SHADER_NAME = "net/sf/ardengine/res/shaders/text";
    public static Shader TEXT_SHADER;
	public static String SHAPE_SHADER_NAME = "net/sf/ardengine/res/shaders/shape";
	public static Shader SHAPE_SHADER;
    public static String COLLISION_SHADER_NAME = "net/sf/ardengine/res/shaders/collision";
    public static Shader COLLISION_SHADER;
	
	private static Matrix viewMatrix;
	
	public ModernOpenGLRenderer(int width, int height) {
		super(width, height);

        if(failedSetup) return;

		//GL setup
		glEnable(GL_TEXTURE_2D);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		glEnable( GL_BLEND );

		viewMatrix = Matrix.buildOrtographicMatrix(width, height);

		//Sane uniforms, different color handling

		if(SPRITE_SHADER == null){
			SPRITE_SHADER = Shader.getShader(SPRITE_SHADER_NAME);
			SPRITE_SHADER.addUniform(new Uniform.Matrix4x4("ortoMatrix", SPRITE_SHADER));
				((Uniform.Matrix4x4)SPRITE_SHADER.getUniform("ortoMatrix")).setValue(viewMatrix);
			SPRITE_SHADER.addUniform(new Uniform.Matrix4x4("transformMatrix", SPRITE_SHADER));
			SPRITE_SHADER.addUniform(new Uniform.Matrix4x4("rotationMatrix", SPRITE_SHADER));
			SPRITE_SHADER.addUniform(new Uniform.Int("imageTexture", SPRITE_SHADER));
				((Uniform.Int)SPRITE_SHADER.getUniform("imageTexture")).setValue(0);
			SPRITE_SHADER.addUniform(new Uniform.Float("transparency", SPRITE_SHADER));
            SPRITE_SHADER.addUniform(new Uniform.Colorf("coloring", SPRITE_SHADER));
                ((Uniform.Colorf)SPRITE_SHADER.getUniform("coloring")).setValue(Color.WHITE);
		}
        if(TEXT_SHADER == null){
            TEXT_SHADER = Shader.getShader(TEXT_SHADER_NAME);
            TEXT_SHADER.addUniform(new Uniform.Matrix4x4("ortoMatrix", TEXT_SHADER));
                ((Uniform.Matrix4x4)TEXT_SHADER.getUniform("ortoMatrix")).setValue(viewMatrix);
            TEXT_SHADER.addUniform(new Uniform.Matrix4x4("transformMatrix", TEXT_SHADER));
            TEXT_SHADER.addUniform(new Uniform.Matrix4x4("rotationMatrix", TEXT_SHADER));
            TEXT_SHADER.addUniform(new Uniform.Int("imageTexture", TEXT_SHADER));
                ((Uniform.Int)TEXT_SHADER.getUniform("imageTexture")).setValue(0);
            TEXT_SHADER.addUniform(new Uniform.Float("transparency", TEXT_SHADER));
            TEXT_SHADER.addUniform(new Uniform.Colorf("coloring", TEXT_SHADER));
                ((Uniform.Colorf)TEXT_SHADER.getUniform("coloring")).setValue(Color.WHITE);
        }

        if(SHAPE_SHADER == null){
            SHAPE_SHADER = Shader.getShader(SHAPE_SHADER_NAME);
            SHAPE_SHADER.addUniform(new Uniform.Matrix4x4("ortoMatrix", SHAPE_SHADER));
                ((Uniform.Matrix4x4)SHAPE_SHADER.getUniform("ortoMatrix")).setValue(viewMatrix);
            SHAPE_SHADER.addUniform(new Uniform.Matrix4x4("transformMatrix", SHAPE_SHADER));
            SHAPE_SHADER.addUniform(new Uniform.Matrix4x4("rotationMatrix", SHAPE_SHADER));
            SHAPE_SHADER.addUniform(new Uniform.Float("transparency", SHAPE_SHADER));
            SHAPE_SHADER.addUniform(new Uniform.Colorf("coloring", SHAPE_SHADER));
                ((Uniform.Colorf)SHAPE_SHADER.getUniform("coloring")).setValue(Color.WHITE);
        }

        if(COLLISION_SHADER == null){
            COLLISION_SHADER = Shader.getShader(COLLISION_SHADER_NAME);
            COLLISION_SHADER.addUniform(new Uniform.Matrix4x4("ortoMatrix", COLLISION_SHADER));
                ((Uniform.Matrix4x4)COLLISION_SHADER.getUniform("ortoMatrix")).setValue(viewMatrix);
            COLLISION_SHADER.addUniform(new Uniform.Colorf("color", COLLISION_SHADER));
                ((Uniform.Colorf)COLLISION_SHADER.getUniform("color")).setValue(Color.WHITE);
        }

        ModernCollisionRenderer.init();
	}

	@Override
	public int getMajorVersion() {
		return GL_MAJOR_VERSION;
	}
	
	@Override
	public int getMinorVersion() {
		return GL_MINOR_VERSION;
	}

	@Override
	protected void viewMatrixChange(){
		viewMatrix = Matrix.buildOrtographicMatrix(getBaseWindowWidth(), getBaseWindowHeight());
		((Uniform.Matrix4x4)SPRITE_SHADER.getUniform("ortoMatrix")).setValue(viewMatrix);
		((Uniform.Matrix4x4)TEXT_SHADER.getUniform("ortoMatrix")).setValue(viewMatrix);
		((Uniform.Matrix4x4)SHAPE_SHADER.getUniform("ortoMatrix")).setValue(viewMatrix);
		((Uniform.Matrix4x4)COLLISION_SHADER.getUniform("ortoMatrix")).setValue(viewMatrix);
	}
	
	@Override
	public ISpriteImpl createSpriteImplementation(InputStream is, Sprite parentSprite) {
		return new ModernOpenGLSpriteImpl(is, parentSprite);
	}
	
	@Override
	public ISpriteImpl createSpriteImplementation(String url, Sprite parentSprite) {
		return new ModernOpenGLSpriteImpl(url, parentSprite);
	}
	
	@Override
	public ISpriteImpl createSpriteImplementation(BufferedImage buf, Sprite parentSprite) {
		return new ModernOpenGLSpriteImpl(buf, parentSprite);
	}

	@Override
	public ISpriteSheetImpl createSpriteSheetImplementation(InputStream is, SpriteSheet parentSprite) {
		return new ModernOpenGLSpriteSheetImpl(is, parentSprite);
	}

    @Override
    public ISpriteSheetImpl createSpriteSheetImplementation(String url, SpriteSheet parentSprite) {
        return new ModernOpenGLSpriteSheetImpl(url, parentSprite);
    }

    @Override
	public ISpriteSheetImpl createSpriteSheetImplementation(BufferedImage buf, SpriteSheet parentSprite) {
		return new ModernOpenGLSpriteSheetImpl(buf, parentSprite);
	}

	@Override
    public IShapeImpl createShapeImplementation(IShape shape) {
        switch(shape.getType()){
            case CIRCLE: return new ModernOpenGLCircleImpl((Circle)shape);
            case POLYGON: return new ModernOpenGLPolygonImpl((Polygon)shape);
            case RECTANGLE: return new ModernOpenGLRectangleImpl((Rectangle)shape);
			case LINES: return new ModernOpenGLLineImpl((Line) shape);
        }
        return null;
    }

    @Override
    public ITextImpl createTextImplementation(IFont font, Text parentText) {
        return new ModernOpenGLTextImpl(parentText);
    }

	@Override
	public void renderCollisions(List<Node> collideables){
		for(Node collideable : collideables){
			if(collideable.isCollideable()){
				IRenderableCollision rc = collideable.getPrivateArea();
				ModernCollisionRenderer.draw(rc);

				for(CollisionShape shape :collideable.getCollisions()){
					ModernCollisionRenderer.draw(shape);
				}

				if(collideable instanceof Group){
					renderCollisions(((Group)collideable).getChildren());
				}
			}
		}
	}

	/**
	 * @return Current ortogonal Matrix of window
     */
	public static Matrix getViewMatrix() {
		return viewMatrix;
	}

	@Override
	public void cleanUp() {
		super.cleanUp();
		ModernCollisionRenderer.free();
	}
}