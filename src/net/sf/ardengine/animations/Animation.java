package net.sf.ardengine.animations;

import net.sf.ardengine.Core;

/**
 * Basic Animation class uniting all methods, which animations should have, when added to AnimationManager.
 */
public abstract class Animation {    
    /**Time form start of animation, when animation will be done.*/
	 protected long stopTime=0;
	 private boolean pause=true;
	 private int repeatCount=0;  
    /**Time form start of animation.*/
	 protected long actTime=0;
    
	 /**
	  * @param duration Duration of animation 
	  * @param repeatCount  Count of animation repeats(-1 = infinite)
	  */
	 public Animation(long duration, int repeatCount) {
		 stopTime=duration;
		 this.repeatCount=repeatCount;
	}
	 
	 /**
	  * True if animation ended and requires to use addAnimation() in start()
	  */
    private boolean isFinished = true;
    
    public void update(){
   	 if(!pause){
   		 long delta = +Core.renderer.getDelta();
   		 actTime=actTime+delta;
   		 
   		 if(actTime<=stopTime){
   			 run(delta);
   		 }else{
   			if(repeatCount>0){
   				repeatCount--;
   				finish();
   				innerRestart(delta);
            }else if(repeatCount==-1){
            	finish();
            	innerRestart(delta);
            }else{
            	 finish();
                AnimationManager.removeAnimation(this);
                isFinished = true;
            }
   		 }
   	 }
    }
    
    /**
     * Method called every update
     */
    public abstract void run(long delta);
    /**
     * Method called after actTime >= stopTime
     */
    public abstract void finish();

    /**(Re)starts the animation(Also adds it to manager, if previously removed).*/
    public void start(){ 
      actTime=0;
      pause=false;
      
      if(isFinished){ 
      	 AnimationManager.addAnimation(this);
      	 isFinished = false;
      }
    }    
    
    /**Restarts animation.*/
    private void innerRestart(long delta){
      actTime-=stopTime;
      pause=false;
      run(delta); 
    }
    
    
    /**Pause the animation.*/
    public void pause(){
      if(!pause){
         pause=true;
      }
    }
    /**Continue in animation, if paused.*/
    public void unpause(){
       if(pause){
          pause=false;
        }
    }  

    /**Removes animation from AnimationManager*/
    public void destroy(){
        AnimationManager.removeAnimation(this);
    }
    
    /**
     * 
     * @return true if animation has ended and has no repeatCounts
     */
    public boolean isFinished() {
		return isFinished;
	}
}
