package net.sf.ardengine.renderer.opengl.legacy.shapes;

import net.sf.ardengine.shapes.Polygon;

import static org.lwjgl.opengl.GL11.*;

public class LegacyOpenGLPolygonImpl extends LegacyOpenGLShapeImpl{

    private Polygon parent;
    private float width;
    private float height;

    private float[] coords;

    public LegacyOpenGLPolygonImpl(Polygon parent){
        this.parent = parent;
        coordsChanged();
    }

    @Override
    public Polygon getParent() {
        return parent;
    }

    @Override
    public void vertexDraw() {

        glBegin(GL_POLYGON);
        for( int i=0; i <coords.length; i+=2){
            glVertex2f(coords[i], coords[i+1]);
        }
        glEnd();
    }

    @Override
    public void coordsChanged() {
        coords = parent.getCoords();

        float minX = coords[0];
        float maxX = coords[0];
        float minY = coords[1];
        float maxY = coords[1];

        for(int i=0; i < coords.length/2; i++){
            if(coords[i*2] < minX ){
                minX = coords[i*2];
            }else if(coords[i*2] > maxX){
                maxX = coords[i*2];
            }

            if(coords[i*2+1] < minY ){
                minY = coords[i*2+1];
            }else if(coords[i*2+1] > maxY){
                maxY = coords[i*2+1];
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