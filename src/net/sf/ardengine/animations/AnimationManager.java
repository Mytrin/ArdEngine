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
         for(Animation animation : animations){
             animation.update();
         }  
         
         for(Animation animation : animationsToRemove){
            animations.remove(animation);
         }
         animationsToRemove.clear();
         
         for(Animation animation : animationsToAdd){
            animations.add(animation);
         }
         animationsToAdd.clear();
         
    } 
    /**Adds animation to manager, so it can be refreshed every loop.
     */
    protected static void addAnimation(Animation animation){
        animationsToAdd.add(animation);
    }
    /**Removes animation from manager, so it will be refreshed no more.
     */
    protected static void removeAnimation(Animation animation){
        animationsToRemove.add(animation);
    }

}