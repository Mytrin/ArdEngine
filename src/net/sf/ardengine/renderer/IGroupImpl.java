package net.sf.ardengine.renderer;

import net.sf.ardengine.Node;

/**
 * Implementation of groups for custom renderers,
 * For GL it is just list with drawables to renderer,
 * however for object oriented FX it has to be FX Group class.
 */
public interface IGroupImpl extends IDrawableImpl{

    /**
     * Automatically called from group
     * @param child child, which has been recently added to parent group
     */
    public void childAdded(Node child);

    /**
     * Automatically called from group
     * @param child child, which has been recently removed from parent group
     */
    public void childRemoved(Node child);
}
