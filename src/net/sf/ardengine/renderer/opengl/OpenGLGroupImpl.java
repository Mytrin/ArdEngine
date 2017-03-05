package net.sf.ardengine.renderer.opengl;

import net.sf.ardengine.Group;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.IGroupImpl;

public class OpenGLGroupImpl implements IGroupImpl {

    Group parentGroup;

    public OpenGLGroupImpl(Group parentGroup){
        this.parentGroup = parentGroup;
    }

    @Override
    public void draw() {
        parentGroup.forEachChildren((child)->{child.draw();});
    }

    @Override
    public void update() {
        parentGroup.forEachChildren((child)->{child.getImplementation().update();});
    }

    @Override
    public void free() {
        parentGroup.forEachChildren((child)->{child.getImplementation().free();});
    }

    @Override
    public void childAdded(Node child) {
        //nothing to do, childupdates itself later on draw()
    }

    @Override
    public void childRemoved(Node child) {
        child.getImplementation().free();
    }
}
