package net.sf.ardengine.renderer.javaFXRenderer;

/**
 * JavaFX uses different group coord system, than Ard,
 * if Node is in Group, it interprets all set*X as setLayoutX.
 * So, group has to call different draw() method.
 **/
public interface IJavaFXGroupable {
    /**
     * Called by group instead of draw()
     */
    public void groupDraw();
}
