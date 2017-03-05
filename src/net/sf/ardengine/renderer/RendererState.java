package net.sf.ardengine.renderer;

/**
 * State of renderer returned by Core.notifyWhenRendererReady() method
 */
public enum RendererState {
    /**Returned by Succesfully created renderer*/
    READY,
    /**Returned by Chosen renderer, if fails*/
    FAILED,
    /**Returned by Fallback renderer, if fails*/
    CRASH
}
