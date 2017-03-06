package net.sf.ardengine.renderer.opengl.modern;

import javafx.scene.paint.Color;
import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.opengl.lib.GLUtils;
import net.sf.ardengine.renderer.opengl.lib.shader.Matrix;
import net.sf.ardengine.renderer.opengl.lib.shader.Shader;
import net.sf.ardengine.renderer.opengl.lib.shader.Uniform;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.IntBuffer;

public abstract class ModernOpenGLRenderable {

    private Matrix rotationMatrix = new Matrix(4, 4);
    private float lastAngle = 0;
    private Matrix transformMatrix = new Matrix(4, 4);

    private IntBuffer vertexArrays;
    protected IntBuffer vertexBuffers;

    protected void init(int customBuffers){

        if(customBuffers < 1) throw new RuntimeException("Incorrect number of vertex buffers!");

        vertexBuffers = BufferUtils.createIntBuffer(customBuffers);
        vertexArrays = BufferUtils.createIntBuffer(1);

        GL30.glGenVertexArrays(vertexArrays);
        GL30.glBindVertexArray(vertexArrays.get(0));
        GL15.glGenBuffers(vertexBuffers);

        customBufferInit();

        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
        GL30.glBindVertexArray(0);
    }

    protected void updateTransformMatrix(){
        float scale = getRenderScale();

        transformMatrix.setValue(0, 0, scale);
        transformMatrix.setValue(1, 1, scale);
        transformMatrix.setValue(2, 2, scale);

        //Coords are from upper left corner

        transformMatrix.setValue(0, 3, (float)(getRenderX() + getRenderWidth()/2) - (!isRenderStatic()? Core.cameraX:0));
        transformMatrix.setValue(1, 3, (float)(getRenderY() + getRenderHeight()/2) - (!isRenderStatic()? Core.cameraY:0));
    }

    protected void updateRotationMatrix(){

        float newAngle = getRenderAngle();

        if(lastAngle !=  newAngle){
            lastAngle = newAngle;
            double lastAngleRad = Math.toRadians(lastAngle);
            rotationMatrix.setValue(0, 0, (float)Math.cos(lastAngleRad));
            rotationMatrix.setValue(1, 1, (float)Math.cos(lastAngleRad));
            rotationMatrix.setValue(1, 0, (float)Math.sin(lastAngleRad));
            rotationMatrix.setValue(0, 1, (float)-Math.sin(lastAngleRad));
        }
    }

    protected void freeBuffers(){
        if(vertexBuffers != null){
            GL15.glDeleteBuffers(vertexBuffers);
            GL30.glDeleteVertexArrays(vertexArrays);
        }
    }

    //CHANGEABLE METHODS
    protected void update(Shader shader){
        updateTransformMatrix();
        ((Uniform.Matrix)shader.getUniform("transformMatrix")).setValue(transformMatrix);
        updateRotationMatrix();
        ((Uniform.Matrix)shader.getUniform("rotationMatrix")).setValue(rotationMatrix);
        ((Uniform.Float)shader.getUniform("transparency")).setValue(getRenderOpacity());
        ((Uniform.Colorf)shader.getUniform("coloring")).setValue(getRenderColor());
    }

    protected void render(Shader shader, int coordCount) {
        update(shader);
        shader.bind();

        GL30.glBindVertexArray(vertexArrays.get(0));

        customBind();

        GL11.glDrawArrays(getDrawMode(), 0, coordCount);
        GLUtils.checkForGLErrors();
    }

    protected void render(Shader shader) {
        render(shader, 4);
    }

    protected void customBufferInit(){}

    protected void customBind(){}

    protected int getDrawMode(){
        return GL11.GL_TRIANGLE_STRIP;
    }

    //IMPLEMENTATION METHODS
    protected abstract float getRenderX();

    protected abstract float getRenderY();

    protected abstract float getRenderAngle();

    protected abstract float getRenderScale();

    protected abstract float getRenderOpacity();

    protected abstract Color getRenderColor();

    protected abstract float getRenderWidth();

    protected abstract float getRenderHeight();

    protected abstract boolean isRenderStatic();
}