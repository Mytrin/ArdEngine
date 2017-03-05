package net.sf.ardengine.renderer.opengl.legacy.shapes;

import net.sf.ardengine.shapes.Line;

import static org.lwjgl.opengl.GL11.*;

public class LegacyOpenGLLineImpl extends LegacyOpenGLShapeImpl {

    private Line parent;
    private float width;
    private float height;

    private float[] coords;

    public LegacyOpenGLLineImpl(Line parent) {
        this.parent = parent;
        coordsChanged();
    }

    @Override
    public Line getParent() {
        return parent;
    }

    @Override
    public void vertexDraw() {
        glLineWidth(parent.getStrokeWidth());
        glBegin(GL_LINE_STRIP);
        for (int i = 0; i < coords.length; i += 2) {
            glVertex2f(coords[i], coords[i + 1]);
        }
        glEnd();
        glLineWidth(1.0f);
    }

    @Override
    public void coordsChanged() {
        coords = parent.getCoords();

        float minX = coords[0];
        float maxX = coords[0];
        float minY = coords[1];
        float maxY = coords[1];

        for (int i = 0; i < coords.length / 2; i++) {
            if (coords[i * 2] < minX) {
                minX = coords[i * 2];
            } else if (coords[i * 2] > maxX) {
                maxX = coords[i * 2];
            }

            if (coords[i * 2 + 1] < minY) {
                minY = coords[i * 2 + 1];
            } else if (coords[i * 2 + 1] > maxY) {
                maxY = coords[i * 2 + 1];
            }
        }

        width = maxX - minX;
        height = maxY - minY;
    }

    @Override
    public float getShapeWidth() {
        return width;
    }

    @Override
    public float getShapeHeight() {
        return height;
    }
}