package net.sf.ardengine.animations;

import net.sf.ardengine.Node;

/**
 * Animation, which changes scale over time.
 */
public class ScaleTransition extends Animation{
    
     private Node targetNode;
     private float wantedScale;
     private float startingScale;
     private float scalePerms;
    
      /**
      * @param node The taget Node 
      * @param duration Duration of animation 
      * @param fromScale Starting node scale(1 = 100%) 
      * @param toScale Final node scale(1 = 100%) 
      * @param repeatCount Count of animation repeats(-1 = infinite)
      */
     public ScaleTransition(Node node, long duration,float fromScale, float toScale,int repeatCount){
   	 super(duration, repeatCount);
       targetNode=node;
       wantedScale=toScale;
       scalePerms=(toScale-fromScale)/duration;
       startingScale=fromScale;
    }
     
     @Override
    public void start(){ //(re)start
      super.start();
      targetNode.setScale(startingScale);
    }    
    
    @Override
    public void run(long delta){
        targetNode.setScale(targetNode.getScale()+scalePerms*delta);
    }
    
    @Override
   public void finish() {
   	 targetNode.setScale(wantedScale);  
   }
} 

