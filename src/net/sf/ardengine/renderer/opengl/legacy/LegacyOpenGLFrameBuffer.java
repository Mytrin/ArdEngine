package net.sf.ardengine.renderer.opengl.legacy;

import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.opengl.legacy.util.LegacyExtensions;
import net.sf.ardengine.renderer.opengl.lib.textures.Texture;
import net.sf.ardengine.renderer.opengl.lib.textures.TextureFormat;

import static org.lwjgl.opengl.ARBFramebufferObject.*;
import static org.lwjgl.opengl.GL11.*;


public class LegacyOpenGLFrameBuffer {

    private static final String EXTENSION_NAME = "GL_ARB_framebuffer_object";

    public static boolean isAvailable(){
        return LegacyExtensions.isExtensionAvailable(EXTENSION_NAME);
    }

    private static int framebuffers = 0;

    private Texture fbTexture;
    private int frameBufferID = 0;

    public LegacyOpenGLFrameBuffer(int width, int height){
        this(width, height, TextureFormat.RGBA_B);
    }

    public LegacyOpenGLFrameBuffer(int width, int height, TextureFormat format){
        if(width <=0 || height <=0) throw new IllegalArgumentException("Attemting to create framebuffer "+width+"x"+height+"!");

        fbTexture = new Texture("fb-texture-"+framebuffers, width, height, null, format);

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

        ((LegacyOpenGLRenderer) Core.renderer).viewMatrixChange();

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
        glBindTexture(GL_TEXTURE_2D, 0);
        glViewport(0,0, fbTexture.getWidth(), fbTexture.getHeight());

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        glOrtho(0, fbTexture.getWidth(), fbTexture.getHeight(), 0, -1f, 1f);
        glMatrixMode(GL_MODELVIEW);

        glBindFramebuffer(GL_FRAMEBUFFER, frameBufferID);

        checkStatus();
    }

    /**
     * Changes rendering buffer to standart
     */
    public void unbind(){
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        ((LegacyOpenGLRenderer) Core.renderer).viewMatrixChange();
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
                }

                throw new RuntimeException("Invalid FrameBuffer status: "+fault);
            }
        }
    }

}