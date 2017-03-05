package net.sf.ardengine.animations;

import net.sf.ardengine.Node;

/**
 * Animation, which changes node location over time.
 */
public class MoveTransition extends Animation{
    private float xDistance;
    private float yDistance;
    private Node targetNode;
    private float xPerms;
    private float yPerms;
    private float endX;
    private float endY;
    
    private boolean firstRun=true;
    
    
      /**
      * @param node The taget Node
      * @param duration Duration of animation 
      * @param endX Final X coord 
      * @param endY Final Y coord
      * @param repeatCount Count of animation repeats(-1 = infinite) 
      */
    public MoveTransition(Node node, long duration,float endX, float endY,int repeatCount){
   	 super(duration, repeatCount);
       this.endX=endX;
       this.endY=endY;
       targetNode=node;
    }
    
    @Override
    public void run(long delta){
          if(!firstRun){
         	 targetNode.setX(targetNode.getX()+(delta*xPerms));
         	 targetNode.setY(targetNode.getY()+(delta*yPerms));
          }else{
           firstRun(); 
        }
    }
    
    @Override
   public void finish() {
   	 targetNode.setX(endX);
        targetNode.setY(endY);
   }
    
    /**
     * Method called after start of this animation, so it can calculate with 
     * coords of target node at the start time.
     */
    private void firstRun(){
        float startX=targetNode.getX();
        float startY=targetNode.getY();
       
       xDistance=endX-startX;
       yDistance=endY-startY;
       
       xPerms=xDistance/stopTime;
       yPerms=yDistance/stopTime;
       firstRun=false;
    }
}
