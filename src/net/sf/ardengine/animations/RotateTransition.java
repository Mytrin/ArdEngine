package net.sf.ardengine.animations;

import net.sf.ardengine.Node;

/**
 * Animation, which changes node rotation over time.
 */
public class RotateTransition extends Animation{
    
     private Node targetNode;
     private float wantedAngle;
     private float startAngle;
     private float anglePerms;
     
     
      /**
      * @param node The taget Node 
      * @param duration Duration of animation 
      * @param angle How much rotate node 
      * @param repeatCount Count of animation repeats(-1 = infinite)
      */
     public RotateTransition(Node node, long duration,float angle,int repeatCount){
   	 super(duration, repeatCount);
       targetNode=node;
       wantedAngle=angle;
       anglePerms=angle/duration;
       startAngle = node.getAngle();
    }
    
    @Override
    public void run(long delta){
   	 targetNode.setAngle((targetNode.getAngle()+delta*anglePerms)%360);
    }
    
    @Override
   public void finish() {
   	 targetNode.setAngle(startAngle + wantedAngle);
   }
} 