package net.sf.ardengine;

import javafx.scene.paint.Color;
import net.sf.ardengine.renderer.IDrawableImpl;

	/**
	 * @author mytrin
	 * Interface for implementing draw ability to objects.
	 */
	public interface IDrawable {
		
		/**
		 * @return implementation of drawable for current renderer
		 */
		public IDrawableImpl getImplementation();

		/**
		 * Draws drawable.
		 */
		public void draw();

		/**
		 * @return X coord of drawable
		 */
		public float getX();
		
		/**
		 * @return Y coord of drawable
		 */
		public float getY();
		
		/**
		 * @return Z coord of drawable (default is 1)
		 */
		public float getZ();
		
		/**
		 * Sets the X coord of drawable
		 */
		public void setX(float newX);
		
		/**
		 * Sets the Y coord of drawable
		 */
		public void setY(float newY);
		
		/**
		 * Sets the Z coord of drawable
		 */
		public void setZ(float newZ);

		/**
		 * Sets X offset used, if this node is part of Group
		 * @param newLayoutX offset from Group coordinates
         */
		public void setLayoutX(float newLayoutX);

        /**
         * @return offset from Group coordinates
         */
        public float getLayoutX();

        /**
         * Sets Y offset used, if this node is part of Group
         * @param newLayoutY offset from Group coordinates
         */
        public void setLayoutY(float newLayoutY);

        /**
         * @return offset from Group coordinates
         */
        public float getLayoutY();

        /**
         * Automatically called from rotated group for rendering drawable as it would be part of group
         * @param newLayoutOffsetX X offset, stacks with LayoutX
         * @param newLayoutOffsetY Y offset, stacks with LayoutY
         */
        public void setLayoutOffsetCoords(float newLayoutOffsetX, float newLayoutOffsetY);

        /**
         * @return X offset from rotated group
         */
        public float getLayoutOffsetX();

        /**
         * @return Y offset from rotated group
         */
        public float getLayoutOffsetY();

		/**
		 * Automatically used by Core
		 * 
		 * Returns offset x used to be render this object on static position.
		 */
		public float getStaticX();
		
		/**
		 * Automatically used by Core
		 * 
		 * Returns offset x used to be render this object on static position.
		 */
		public float getStaticY();
		
		/**
		 * Automatically used by Core
		 * 
		 * Sets offset x used to be render this object on static position.
		 */
		public void setStaticX(float newStaticX);
		
		/**
		 * Automatically used by Core
		 * 
		 * Sets offset x used to be render this object on static position.
		 */
	    public void setStaticY(float newStaticY);
	    
	    /**
	     * Automatically used by Core
	     * 
	     * @return true if drawable automatically changes position with moving game camera.
	     */
	    public boolean isStatic();
	    
	    /**
	     * 
	     * If true, drawable automatically changes position with moving game camera.
	     */
	    public void setStatic(boolean staticLock);

        /**
         * @return Total width of this drawable
         */
        public float getWidth();

        /**
         * @return Total height of this drawable
         */
        public float getHeight();

        /**
         * Changes the opacity of this drawable
         */
        public void setOpacity(float newOpacity);

        /**
         * Returns the opacity of this drawable
         */
        public float getOpacity();

        /**
         * Changes the scale of this drawable (1 is default)
         */
        public void setScale(float newScale);

        /**
         * Returns the scale of this drawable (1 is default)
         */
        public float getScale();

        /**
         * Sets the angle by which is drawable rotated around its center( clockwise)
         */
        public void setAngle(float angle);

        /**
         * Returns the angle by which is drawable rotated around its center( clockwise)
         */
        public float getAngle();

        /**
         * @return Color of this text
         */
        public Color getColor();

        /**
         * Chages color of this text.
         * @param color new color
         */
        public void setColor(Color color);
	}