package net.sf.ardengine.animations;

import net.sf.ardengine.Node;

/**
 * Animation, which changes node opacity over time.
 */
public class FadeTransition extends Animation{
    
     private Node targetNode;
     private float wantedOpacity;
     private float startingOpacity;
     private float opacityPerms;
    
     
     /**
      * @param node The taget Node 
      * @param duration Duration of animation 
      * @param fromOpacity Starting node opacity
      * @param toOpacity Final node opacity 
      * @param repeatCount Count of animation repeats(-1 = infinite) 
      */
     public FadeTransition(Node node, long duration,float fromOpacity, float toOpacity, int repeatCount){
   	 super(duration, repeatCount);
       targetNode=node;
       wantedOpacity=toOpacity;
       opacityPerms=(toOpacity-fromOpacity)/duration;
       startingOpacity=fromOpacity;
    }
     
     @Override
    public void start(){ //(re)start
      super.start();
      targetNode.setOpacity(startingOpacity);
    }    
    
    @Override
    public void run(long delta){
   	 targetNode.setOpacity(targetNode.getOpacity()+opacityPerms*delta);
    }
    
    @Override
   public void finish() {
   	 targetNode.setOpacity(wantedOpacity); 
   }
}
