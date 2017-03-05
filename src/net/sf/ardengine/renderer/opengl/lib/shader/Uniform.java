package net.sf.ardengine.renderer.opengl.lib.shader;

import static org.lwjgl.opengl.GL20.*;

import java.nio.FloatBuffer;

import javafx.scene.paint.Color;
import org.lwjgl.BufferUtils;

public abstract class Uniform<T>{
	/**Name of uniform in shader*/
	protected String name;
	/**GL handle for this uniform*/
	protected int id;
	/**Uniform value*/
	protected T value;
	/**Target shader*/
	protected Shader shader;
	
	/**
	 * @param name Name of uniform in shader
	 * @param program GL handle for this uniform
	 */
	public Uniform(String name, Shader program){
		this.name = name;
		this.shader = program;
		this.id = glGetUniformLocation(program.getProgramID(), name);
		if (this.id==-1) {
			System.out.println("Variable \""+name+"\" is not found in shader");
		}
	}
	
	/**
	 * @return name of this uniform
	 */
	public String getName() {
		return name;
	}
	/**
	 * binds this uniform for its shader program
	 */
	public abstract void bind();
	
	/**
	 * Changes value of this uniform and makes GL call, if needed.
	 * @param value new value of uniform
	 */
	public void setValue(T value) {
		if(this.value != value){
			this.value = value;
			
			if(shader.isBound()){ //refreshes this uniform for shader
				bind();
			}
		}
	}
	
	public static class Float extends Uniform<java.lang.Float>{
		
		public Float(String name, Shader program) {
			super(name, program);
		}
		
		@Override
		public void bind() {
			glUniform1f(id, value);
		}
	}
	
	public static class Int extends Uniform<java.lang.Integer>{
		
		public Int(String name, Shader program) {
			super(name, program);
		}
		
		@Override
		public void bind() {
			glUniform1i(id, value);
		}
	}
	
	public abstract static class Matrix extends Uniform<net.sf.ardengine.renderer.opengl.lib.shader.Matrix>{
		
		public Matrix(String name, Shader program) {
			super(name, program);
		}
	}
	
	public static class Matrix4x4 extends Matrix{
		
		public Matrix4x4(String name, Shader program) {
			super(name, program);
		}

		@Override
		public void bind() {
			FloatBuffer data = BufferUtils.createFloatBuffer(16);
							data.put(value.getValues());
							data.flip();
			
			glUniformMatrix4fv(id, false, data);
		}
	}

	public static class Vec3f extends Uniform<float[]>{

		public Vec3f(String name, Shader program) {
			super(name, program);
		}

		@Override
		public void bind() {
			glUniform3f(id, value[0], value[1], value[2]);
		}
	}

    public static class Colorf extends Uniform<Color>{

        public Colorf(String name, Shader program) {
            super(name, program);
        }

        @Override
        public void bind() {
            glUniform3f(id, (float)value.getRed(), (float)value.getGreen(), (float)value.getBlue());
        }
    }
	
}
