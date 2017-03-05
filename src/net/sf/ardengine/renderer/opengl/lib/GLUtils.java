package net.sf.ardengine.renderer.opengl.lib;

import static org.lwjgl.opengl.GL11.*;

public class GLUtils {

	private GLUtils(){}
	
	/**
	 * If invoked on GL thread, prints all GL errors occured
	 * after last call.
	 */
	public static void checkForGLErrors(){
		int err;
		while((err = glGetError()) != GL_NO_ERROR){
			switch(err){
				default: System.err.println(err);
							break;
			}
		}
	}
	
}
