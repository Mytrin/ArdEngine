package net.sf.ardengine.renderer.opengl.lib.shader;

import java.util.logging.Logger;

public class Matrix {
	
	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	protected float[] values;
	
	protected int cols;
	protected int rows;
	
	/**
	 * @param cols width
	 * @param rows height
	 */
	public Matrix(int cols, int rows) {
		this.cols = cols;
		this.rows = rows;
		values = new float[cols*rows];
		
		for(int x=0; x < cols; x++){
			if(x < rows){
				values[x*cols + x]=1;
			}
		}
	}
	
	/**
	 * @return reference to the array with values
	 */
	public float[] getValues(){
		return values;
	}
	
	public void setValues(float[] values){
		int requiredLength = cols*rows;
		if(values.length == requiredLength){
			this.values = values;
		}else{
			for(int i=0; i<(values.length%(requiredLength+1)); i++){
				this.values[i] = values[i];
			}
		}
	}
	
	/**
	 * @param x column
	 * @param y row
	 * @param value new value of cell
	 */
	public void setValue(int x, int y, float value){
		int index = y*cols + x;
		if(index < values.length){
			values[index] = value;
		}else{
			LOGGER.warning("Accesing index "+index+" in "+values.length+" Matrix");
		}
	}
	
	/**
	 * 
	 * @param x column
	 * @param y row
	 * @return value of cell or -1, if coords are larger than array
	 */
	public float getValue(int x, int y){
		int index = y*cols + x;
		if(index < values.length){
			return values[index];
		}else{
			LOGGER.warning("Accesing index "+index+" in "+values.length+" Matrix");
			return -1;
		}
	}
	
	public static float[] IDENTITY_MATRIX_DATA = new float[]{
		1, 0, 0, 0,
		0, 1, 0, 0,
		0, 0, 1, 0,
		0, 0, 0, 1
	};

	public static Matrix IDENTITY_MATRIX4x4 = new Matrix(4,4);
		static{
			IDENTITY_MATRIX4x4.setValues(IDENTITY_MATRIX_DATA);
		}
	
	@Override
	public String toString() {
		String toReturn =  super.toString()+":\n";
		
		for(int y = 0; y < rows; y++){
			for(int x = 0; x < cols; x++){
				toReturn+=values[y*cols+x]+" ";
			}
			toReturn+="\n";
		}
		
		return toReturn;
	}
	
	/**
	 * @return 2D 4x4 ortographic matrix
	 */
	public static Matrix buildOrtographicMatrix(float width, float height){
		Matrix toReturn = new Matrix(4, 4);
		toReturn.setValues(new float[]{2f/(width),  0, 	  	     0, 0, 
						   	 				 0, 	       -2f/height,  0, 0, 
						   	 				 0, 	   	  0, 	 	     0, 0,
						   	 				-1,	  	     1,		     0, 1});

		return toReturn;
	}

	/**
	 * @return 3D 4x4 ortographic matrix
	 */
	public static Matrix buildOrtographicMatrix(float width,  float height, float zNear,  float zFar) {
		Matrix toReturn = new Matrix(4, 4);
		toReturn.setValues(  new float[] { 2f / width,	0f, 					0f, 				 			 			0f,
							 						  0f,		  	  -2f / height,   	0f, 							 			0f, 
							 						  0f, 			0f, 		   	  -2f / (zFar - zNear), 			 	0f,
							 						 -1f,		 		1f,					(zFar + zNear) / (zFar - zNear), 1f });
		return toReturn;
	}
}
