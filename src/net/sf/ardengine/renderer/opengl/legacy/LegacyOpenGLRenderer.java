package net.sf.ardengine.renderer.opengl.legacy;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;

import net.sf.ardengine.Node;
import net.sf.ardengine.Sprite;
import net.sf.ardengine.SpriteSheet;
import net.sf.ardengine.collisions.CollisionShape;
import net.sf.ardengine.renderer.ISpriteSheetImpl;
import net.sf.ardengine.renderer.opengl.OpenGLRenderer;
import net.sf.ardengine.renderer.opengl.legacy.shapes.LegacyOpenGLCircleImpl;
import net.sf.ardengine.renderer.opengl.legacy.shapes.LegacyOpenGLLineImpl;
import net.sf.ardengine.renderer.opengl.legacy.shapes.LegacyOpenGLPolygonImpl;
import net.sf.ardengine.renderer.opengl.legacy.shapes.LegacyOpenGLRectangleImpl;
import net.sf.ardengine.renderer.ISpriteImpl;
import net.sf.ardengine.renderer.opengl.legacy.util.LegacyCollisionRenderer;
import net.sf.ardengine.renderer.opengl.modern.ModernOpenGLSpriteImpl;
import net.sf.ardengine.renderer.opengl.modern.ModernOpenGLSpriteSheetImpl;
import net.sf.ardengine.renderer.util.IRenderableCollision;
import net.sf.ardengine.shapes.*;
import net.sf.ardengine.text.IFont;
import net.sf.ardengine.text.ITextImpl;
import net.sf.ardengine.text.Text;

import static org.lwjgl.opengl.GL11.*;

public class LegacyOpenGLRenderer extends OpenGLRenderer{
	
		private static final int GL_MAJOR_VERSION = 1;
		private static final int GL_MINOR_VERSION = 4;
	
		public LegacyOpenGLRenderer(int width, int height) {
			super(width, height);

            if(failedSetup) return;

            viewMatrixChange();

            glEnable(GL_TEXTURE_2D);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glEnable( GL_BLEND );
        }
		
		@Override
		public int getMajorVersion() {
			return GL_MAJOR_VERSION;
		}
		
		@Override
		public int getMinorVersion() {
			return GL_MINOR_VERSION;
		}

        public void viewMatrixChange(){
            glViewport(0, 0, getWindowWidth(), getWindowHeight());
            glMatrixMode(GL_PROJECTION);
            glLoadIdentity();
            glOrtho(0.0f, window.getBaseWidth(), window.getBaseHeight(), 0.0f, -1f, 1f);
            glMatrixMode(GL_MODELVIEW);
		    glLoadIdentity();
        }

		@Override
		public ISpriteImpl createSpriteImplementation(InputStream is, Sprite parentSprite) {
		    return new LegacyOpenGLSpriteImpl(is, parentSprite);
		}
		
		@Override
		public ISpriteImpl createSpriteImplementation(String url, Sprite parentSprite) {
            return new LegacyOpenGLSpriteImpl(url, parentSprite);
		}
		
		@Override
		public ISpriteImpl createSpriteImplementation(BufferedImage buf, Sprite parentSprite) {
            return new LegacyOpenGLSpriteImpl(buf, parentSprite);
		}


	@Override
	public ISpriteSheetImpl createSpriteSheetImplementation(InputStream is, SpriteSheet parentSprite) {
		return new LegacyOpenGLSpriteSheetImpl(is, parentSprite);
	}

	@Override
	public ISpriteSheetImpl createSpriteSheetImplementation(String url, SpriteSheet parentSprite) {
		return new LegacyOpenGLSpriteSheetImpl(url, parentSprite);
	}

	@Override
	public ISpriteSheetImpl createSpriteSheetImplementation(BufferedImage buf, SpriteSheet parentSprite) {
		return new LegacyOpenGLSpriteSheetImpl(buf, parentSprite);
	}

	@Override
	public ITextImpl createTextImplementation(IFont font, Text parentText) {
        return new LegacyOpenGLTextImpl(parentText);
	}

	@Override
	public IShapeImpl createShapeImplementation(IShape shape) {
        switch(shape.getType()){
            case CIRCLE: return new LegacyOpenGLCircleImpl((Circle)shape);
            case POLYGON: return new LegacyOpenGLPolygonImpl((Polygon)shape);
            case RECTANGLE: return new LegacyOpenGLRectangleImpl((Rectangle)shape);
			case LINES: return new LegacyOpenGLLineImpl((Line)shape);
        }
        return null;
	}

    @Override
	public void renderCollisions(List<Node> collideables){
        for(Node collideable : collideables){
			if(collideable.isCollideable()){
                IRenderableCollision rc = collideable.getPrivateArea();
                LegacyCollisionRenderer.drawLines(rc.getLineCoordinates(), rc.getLineColor());

                for(CollisionShape shape :collideable.getCollisions()){
                    LegacyCollisionRenderer.drawLines(shape.getLineCoordinates(), shape.getLineColor());
                }
            }
		}
	}
}