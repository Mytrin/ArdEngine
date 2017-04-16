package net.sf.ardengine.renderer.opengl.modern.util;

import javafx.scene.paint.Color;
import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.opengl.lib.shader.Shader;
import net.sf.ardengine.renderer.opengl.lib.shader.Uniform;
import net.sf.ardengine.renderer.opengl.modern.ModernOpenGLRenderer;
import net.sf.ardengine.renderer.util.IRenderableCollision;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL30;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glBindVertexArray;

/**
 * Simple singleton for rendering lines of collision shapes
 */
public class ModernCollisionRenderer {

    private static LineRenderable lineRenderer = new LineRenderable();

    private ModernCollisionRenderer(){};

    public static void init(){
        lineRenderer.init();
    }

    public static void free(){
        lineRenderer.free();
    }

    public static void draw(IRenderableCollision collision){
        lineRenderer.draw(collision);
    }

    private static class LineRenderable {

        private IntBuffer vertexArrays;
        protected IntBuffer vertexBuffers;

        private float[] coords;
        private Color color;

        public void draw(IRenderableCollision collision){
            this.coords = collision.getLineCoordinates();
            for(int i=0; i < coords.length; i+=2){
                coords[i] -= Core.cameraX;
                coords[i+1] -= Core.cameraY;
            }
            this.color = collision.getLineColor();

            FloatBuffer vertexPositionsBuffer = BufferUtils.createFloatBuffer(coords.length+2);
            vertexPositionsBuffer.put(coords);
            vertexPositionsBuffer.put(coords[0]); //GL_LINE_STRIP
            vertexPositionsBuffer.put(coords[1]);
            vertexPositionsBuffer.flip();

            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(0));
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexPositionsBuffer, GL15.GL_STATIC_DRAW);

            ((Uniform.Colorf) ModernOpenGLRenderer.COLLISION_SHADER.getUniform("color")).setValue(color);

            render(ModernOpenGLRenderer.COLLISION_SHADER);
        }

        protected void init(){
            vertexBuffers = BufferUtils.createIntBuffer(1);
            vertexArrays = BufferUtils.createIntBuffer(1);

            GL30.glGenVertexArrays(vertexArrays);
            glBindVertexArray(vertexArrays.get(0));
            GL15.glGenBuffers(vertexBuffers);

            glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
            glBindVertexArray(0);
        }

        protected void render(Shader shader) {
            shader.bind();

            glBindVertexArray(vertexArrays.get(0));

            glEnableVertexAttribArray(0);
            glBindBuffer(GL15.GL_ARRAY_BUFFER, vertexBuffers.get(0));
            glVertexAttribPointer(
                    0,                 	//must match the layout in the shader!
                    2,                 	// size
                    GL11.GL_FLOAT,     	// type
                    false,           		// normalized?
                    0,                  	// stride
                    0            			// array buffer offset
            );

            //GL_LINE_STRIP
            GL11.glDrawArrays(GL_LINE_STRIP, 0, (coords.length+2)/2);
        }

        public  void free(){
            GL15.glDeleteBuffers(vertexBuffers);
            GL30.glDeleteVertexArrays(vertexArrays);
        }
    }

}