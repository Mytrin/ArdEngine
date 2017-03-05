package net.sf.ardengine.renderer.opengl.legacy.shapes;

import javafx.scene.paint.Color;
import net.sf.ardengine.Core;
import net.sf.ardengine.shapes.IShape;
import net.sf.ardengine.shapes.IShapeImpl;

import static org.lwjgl.opengl.GL11.*;

public abstract class LegacyOpenGLShapeImpl implements IShapeImpl {

    public abstract IShape getParent();

    public abstract void vertexDraw();

    @Override
    public void draw() {
        //I have no idea, why I have to disable textures,
        //should I want color rendered properly,
        // and at this point I am too afraid to ask...
        glDisable(GL_TEXTURE_2D);
        float scale = getParent().getScale();
        float opacity = getParent().getOpacity();
        float angle = getParent().getAngle();
        float x = getParent().getX() - (getParent().isStatic()?0: Core.cameraX);
        float y = getParent().getY() - (getParent().isStatic()?0: Core.cameraY);
        float height = getShapeHeight();
        float width = getShapeWidth();
        Color coloring = getParent().getColor();

        glPushMatrix();

        // translate to the right location
        if (scale == 1) {
            glTranslatef(x, y, 0);
        } else {
            glTranslatef(x - width * (scale - 1) / 2, y - height * (scale - 1) / 2, 0);
        }

        glColor4f((float)coloring.getRed(), (float)coloring.getGreen(), (float)coloring.getBlue(), opacity);

        glScalef(scale, scale, 0);
        // do the rotation
        glTranslatef(width / 2, height / 2, 0);
        glRotatef(angle, 0, 0, 1);
        glTranslatef(-width / 2, -height / 2, 0);

        vertexDraw();

        glPopMatrix();
        glEnable(GL_TEXTURE_2D);
    }

    @Override
    public void update() {
        //solved by updateCoords()
    }

    @Override
    public void free() {
        //nothing to do
    }

}