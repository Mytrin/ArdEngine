package net.sf.ardengine.renderer.opengl.modern;

import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.opengl.lib.textures.Texture;
import net.sf.ardengine.renderer.opengl.lib.textures.TextureFormat;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL32.GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS;
import static org.lwjgl.opengles.GLES20.GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS;

public class ModernOpenGLFrameBuffer {

    private static int framebuffers = 0;

    private Texture fbTexture;
    private int frameBufferID = 0;

    public ModernOpenGLFrameBuffer(int width, int height){
        this(width, height, TextureFormat.RGBA_B);
    }

    public ModernOpenGLFrameBuffer(int width, int height, TextureFormat textureFormat){
        if(width <=0 || height <=0) throw new IllegalArgumentException("Attemting to create framebuffer "+width+"x"+height+"!");

        fbTexture = new Texture("fb-texture-"+framebuffers, width, height, null, textureFormat);

        fbTexture.bind();

        frameBufferID = glGenFramebuffers();

        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fbTexture.getId(), 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        framebuffers++;
    }

    /**
     * Frees framebuffer, but holds texture..
     * @return FB texture
     */
    public Texture surrenderTexture(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        glViewport(0,0, Core.renderer.getWindowWidth(), Core.renderer.getWindowHeight());

        glDeleteFramebuffers(frameBufferID);
        return fbTexture;
    }

    /**
     * Frees texture and framebuffer
     */
    public void destroy(){
        fbTexture.free();
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glDeleteFramebuffers(frameBufferID);
    }

    /**
     * Changes rendering buffer to this framebuffer and checks its status
     */
    public void bind(){
        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);

        glViewport(0,0, fbTexture.getWidth(), fbTexture.getHeight());

        checkStatus();
    }

    /**
     * Changes rendering buffer to standart
     */
    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0,0, Core.renderer.getWindowWidth(), Core.renderer.getWindowHeight());
    }

    /**
     * @return Texture with rendered data
     */
    public Texture getFbTexture() {
        return fbTexture;
    }

    private void  checkStatus(){
        int status = glCheckFramebufferStatus(GL_FRAMEBUFFER);

        if(status != GL_FRAMEBUFFER_COMPLETE){
            if(status == GL_FRAMEBUFFER_UNSUPPORTED){
                //this is BAD
                throw new RuntimeException("FrameBuffer is not supported!");
            }else {
                //won't happen unless someone messes with render buffers
                String fault = "UNKNOWN";

                switch(status){
                    case GL_FRAMEBUFFER_UNDEFINED: fault = "GL_FRAMEBUFFER_UNDEFINED";
                                                    break;
                    case GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT: fault = "GL_FRAMEBUFFER_INCOMPLETE_ATTACHMENT";
                        break;
                    case GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT: fault = "GL_FRAMEBUFFER_INCOMPLETE_MISSING_ATTACHMENT";
                        break;
                    case GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER: fault = "GL_FRAMEBUFFER_INCOMPLETE_DRAW_BUFFER";
                        break;
                    case GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER: fault = "GL_FRAMEBUFFER_INCOMPLETE_READ_BUFFER";
                        break;
                    case GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE: fault = "GL_FRAMEBUFFER_INCOMPLETE_MULTISAMPLE";
                        break;
                    case GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS : fault = "GL_FRAMEBUFFER_INCOMPLETE_LAYER_TARGETS ";
                        break;
                    case GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS: fault = "GL_FRAMEBUFFER_INCOMPLETE_DIMENSIONS ";
                        break;
                }

                throw new RuntimeException("Invalid FrameBuffer status: "+fault);
            }
        }
    }
}
