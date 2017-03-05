package net.sf.ardengine.renderer.opengl.lib.shader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.lwjgl.opengl.GL20;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL11.*;

public class ShaderControl {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private int vertexShaderID;
	private int fragmentShaderID;
	private int shaderProgram;
	private String[] vsrc;
	private String[] fsrc;

	/**
	 * Creates new ShaderControl prepared for initialization
	 * 
	 * @param vertexShaderName name of file containing vertex shader
	 * @param fragmentShaderName name of file containing fragment shader
	 */
	public ShaderControl(String vertexShaderName, String fragmentShaderName) {
		vsrc = loadShader(vertexShaderName);
		fsrc = loadShader(fragmentShaderName);
	}
	
	/**
	 * Loads shaders to GPU, must be invoked on GLFW thread
	 * 
	 * @return true if succes
	 */
	public boolean init(){
		try	{
			attachShaders();
			return true;
		} catch (Exception e) {
			LOGGER.severe("Shader initialization failed: "+ e);
			return false;
		}
	}

	/**
	 * 
	 * @param name of shader file to be loaded
	 * @return content of file
	 */
	protected String[] loadShader(String name){
		StringBuilder sb = new StringBuilder();
		try{
			InputStream is = ShaderControl.class.getClassLoader().getResourceAsStream(name);
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			String line;
			while ((line = br.readLine()) != null)
			{
				sb.append(line);
				sb.append('\n');
			}
			is.close();
			LOGGER.info("Loaded shader: "+ name);
		}
		catch (Exception e){
			LOGGER.severe("Shader loading failed: "+ e);
		}
		return new String[]{sb.toString()};
	}

	/**
	 * Attempt to compile shaders
	 * @throws Exception
	 */
	private void attachShaders() throws Exception{
		
		vertexShaderID = glCreateShader(GL20.GL_VERTEX_SHADER);
		
		glShaderSource(vertexShaderID,  vsrc);
		glCompileShader(vertexShaderID);
		if(checkForError(vertexShaderID, GL_COMPILE_STATUS)){
			throw new Exception("Vertex shader compile error!");
		}
		
		fragmentShaderID = glCreateShader(GL20.GL_FRAGMENT_SHADER);
		glShaderSource(fragmentShaderID, fsrc);
		glCompileShader(fragmentShaderID);
		if(checkForError(fragmentShaderID, GL_COMPILE_STATUS)){
			throw new Exception("Fragment shader compile error!");
		}
		
		shaderProgram = glCreateProgram();

		glAttachShader(shaderProgram, vertexShaderID);
		glAttachShader(shaderProgram, fragmentShaderID);
		glLinkProgram(shaderProgram);
		glValidateProgram(shaderProgram);
		
		if(checkProgramForError(shaderProgram, GL_LINK_STATUS)){
			throw new Exception("Program link error!");
		}
	}
	
	/**
	 * @param shaderID id of tested shader
	 * @return true if error has been detected
	 */
	private boolean checkForError(int shaderID, int status){
	 	if(glGetShaderi(shaderID, GL_INFO_LOG_LENGTH) != 0){
	 		LOGGER.info("Shader loading shader message: "+ glGetShaderInfoLog(shaderID));
	 	}
		
	 	if(glGetShaderi(shaderID, status) != GL_TRUE){
	 		return true;
	 	}
	 	
	 	return false;
	}
	
	/**
	 * @param programID id of tested shader
	 * @return true if error has been detected
	 */
	private boolean checkProgramForError(int programID, int status){
	 	if(glGetProgrami(programID, GL_INFO_LOG_LENGTH) != 0){
	 		LOGGER.info("Shader loading program message: "+ glGetShaderInfoLog(programID));
	 	}
		
	 	if(glGetProgrami(programID, status) != GL_TRUE){
	 		return true;
	 	}
	 		 	
	 	return false;
	}

	/**
	 * Activates loaded shaders
	 */
	public int useShader(){
		glUseProgram(shaderProgram);
		return shaderProgram;
	}

	/**
	 * Deactivates shaders
	 */
	public void dontUseShader(){
		glUseProgram(0);
	}

	/**
	 * @return Shader Program ID
	 */
	public int getShaderProgram() {
		return shaderProgram;
	}

	/**
	 * @return Vertex Shader ID
	 */
	public int getVertexShaderProgram() {
		return vertexShaderID;
	}

	/**
	 * @return Fragment Shader ID
	 */
	public int getFragmentShaderProgram() {
		return fragmentShaderID;
	}

	/**
	 * Frees GL ids
	 */
	public void delete(){
		glUseProgram(shaderProgram);
		glDetachShader(shaderProgram, vertexShaderID);
	 	glDetachShader(shaderProgram, fragmentShaderID);

	 	glDeleteShader(vertexShaderID);
	 	glDeleteShader(fragmentShaderID);
	 	glDeleteProgram(shaderProgram);
	}
}
