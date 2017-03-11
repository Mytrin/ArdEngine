package net.sf.ardengine.animations;

import java.util.LinkedList;
import java.util.List;

/**
 * Basic manager for animation handling.
 * Every loop handles all animations in automatically called method animate().
 */
public class AnimationManager {
    private static final List<Animation> animations=new LinkedList<>();
    private static final List<Animation> animationsToAdd=new LinkedList<>();
    private static final List<Animation> animationsToRemove=new LinkedList<>();
    
    public static void animate(){
        animations.removeAll(animationsToRemove);
        animationsToRemove.clear();

        animations.addAll(animationsToAdd);
        animationsToAdd.clear();

        animations.forEach((Animation animation)->animation.update());
    } 
    /**Adds animation to manager, so it can be refreshed every loop.
     * @param animation added animation
     */
    protected static void addAnimation(Animation animation){
        animationsToAdd.add(animation);
    }
    /**Removes animation from manager, so it will be refreshed no more.
     * @param animation removed animation
     */
    protected static void removeAnimation(Animation animation){
        animationsToRemove.add(animation);
    }

}