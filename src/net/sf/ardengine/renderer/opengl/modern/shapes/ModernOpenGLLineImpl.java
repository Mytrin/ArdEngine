package net.sf.ardengine.renderer.opengl.modern.shapes;

import net.sf.ardengine.shapes.IShape;
import net.sf.ardengine.shapes.Line;

import static org.lwjgl.opengl.GL11.GL_LINE_STRIP;
import static org.lwjgl.opengl.GL11.glLineWidth;

public class ModernOpenGLLineImpl extends ModernOpenGLShapeImpl{

    private Line parent;
    private float width;
    private float height;

    public ModernOpenGLLineImpl(Line parent){
        this.parent = parent;
    }

    @Override
    protected float[] createCoords() {

        float[] coords = parent.getCoords();

        if(coords.length < 2) return null;

        float minX = coords[0];
        float maxX = coords[0];
        float minY = coords[1];
        float maxY = coords[1];

        float[] glCoords = new float[coords.length*2];

        for(int i=0; i < coords.length/2; i++){
            glCoords[i*4] = coords[i*2];
            glCoords[i*4+1] = coords[i*2+1];
            glCoords[i*4+2] = 0;
            glCoords[i*4+3] = 1;

            if(glCoords[i*4] < minX ){
                minX = glCoords[i*4];
            }else if(glCoords[i*4] > maxX){
                maxX = glCoords[i*4];
            }

            if(glCoords[i*4+1] < minY ){
                minY = glCoords[i*4+1];
            }else if(glCoords[i*4+1] > maxY){
                maxY = glCoords[i*4+1];
            }
        }

        width = maxX - minX;
        height = maxY - minY;

        float hWidth = (float)width/2;
        float hHeight = (float)height/2;

        for(int i=0; i < glCoords.length/4; i++){
            glCoords[i*4] -=hWidth;
            glCoords[i*4+1] -= hHeight;
        }

        return glCoords;
    }

    @Override
    protected int getDrawMode() {
        return GL_LINE_STRIP;
    }

    @Override
    protected void customBind() {
        super.customBind();
        glLineWidth(parent.getStrokeWidth());
    }

    @Override
    public void postDraw() {
        glLineWidth(1.0f);
    }

    @Override
    public IShape getParent() {
        return parent;
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