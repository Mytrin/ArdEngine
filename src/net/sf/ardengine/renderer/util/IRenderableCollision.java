package net.sf.ardengine.renderer.util;

import javafx.scene.paint.Color;

/**
 * Interface for collision shapes and private area, which enables
 * renderers to render them
 */
public interface IRenderableCollision {

    /**
     * @return color of rendered line
     */
    public Color getLineColor();

    /**
     *
     * @return points to render by renderer in DEBUG mode
     */
    public float[] getLineCoordinates();

}
