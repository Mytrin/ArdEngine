package net.sf.ardengine.renderer.opengl.lib.shader;

import java.util.HashMap;
import java.util.logging.Logger;

public class Shader {

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	public static final String FRAGMENT_ATTACHMENT = ".frag.glsl";
	public static final String VERTEX_ATTACHMENT = ".vert.glsl";
	/**Loaded shaders*/
	private static HashMap<String, Shader> loadedShaders =  new HashMap<>();
	/**Keeping actual shader to prevent redudant GL calls*/
	private static Shader actualShader = null;

	/**GL handle for shaders*/
	protected ShaderControl shaderControl;
	@SuppressWarnings("rawtypes")
	/**map with uniforms for shader*/
	protected HashMap<String, Uniform> uniforms = new HashMap<>();
	/**flag, that shaders were succesfuly loaded*/
	private final boolean succes;
	/**
	 * 
	 * @param name Name of the shader to be loaded without file attachments, which must be either FRAGMENT_ATTACHMENT or VERTEX_ATTACHMENT
	 */
	public Shader(String name) {
		shaderControl = new ShaderControl(name+VERTEX_ATTACHMENT, name+FRAGMENT_ATTACHMENT);
		succes = shaderControl.init();
	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * Binds this shader program and its uniforms, if not already bound
	 * Must be called on GL thread
	 */
	public void bind(){
		if(actualShader != this){
			shaderControl.useShader();
			for(Uniform uniform : uniforms.values()){
				uniform.bind();
			}
			actualShader = this;
		}
	}
	
	/**
	 * @return true, if shader is bound to current GL context
	 */
	public boolean isBound(){
		return actualShader==this;
	}
	
	/**
	 * @return true, if shader initialization has been succesful
	 */
	public boolean isSucces() {
		return succes;
	}
	
	/**
	 * @return GL id for this shader
	 */
	public int getProgramID(){
		return shaderControl.getShaderProgram();
	}

	@SuppressWarnings("rawtypes")
	/**
	 * @param name name of uniform
	 * @return Uniform, if present
	 */
	public Uniform getUniform(String name){
		return uniforms.get(name);
	}
	
	@SuppressWarnings("rawtypes")
	/**
	 * @param value new uniform
	 */
	public void addUniform(Uniform value){
		uniforms.put(value.getName(), value);
	}
	
	/**
	 * Loads shader if it's not present
	 * 
	 * @param name => name.frag.glsl; name.vert.glsl
	 * @return shader with given name or null, if cannot be found
	 */
	public static Shader getShader(String name){
		Shader toReturn = loadedShaders.get(name);
		
		if(toReturn != null){
			return toReturn;
		}else{
			toReturn = new Shader(name);
			//tries to load shader, if fails, returns null
			if(toReturn.isSucces()){
				LOGGER.info("Created shader "+name);
				loadedShaders.put(name, toReturn);
				return toReturn;
			}else{
				return null;
			}
		}
	}
	
}