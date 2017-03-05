package net.sf.ardengine.shapes;

import javafx.scene.paint.Color;
import net.sf.ardengine.IDrawable;

public interface IShape extends IDrawable{
	/**
	 * @return coords, which define this Shape
	 */
	public float[] getCoords();

    /**
     * @return type of shape for renderer to select appropriate implementation
     */
	public ShapeType getType();
}