package net.sf.ardengine.animations;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Animation, which calls given Frames methods whatToDo(), when their time comes.
 */
public class FrameAnimation extends Animation{    
    /**Frames to do  yet*/
    private List<Frame> keyFrames;
    /**Already done frames */
    private List<Frame> doneKeyFrames = new LinkedList<>();
    
      /**
      * @param duration Duration of animation
      * @param keyFrames Frames to do 
      * @param repeatCount Count of animation repeats(-1 = infinite) 
      */
    public FrameAnimation(long duration, LinkedList<Frame>keyFrames,int repeatCount){
   	 super(duration, repeatCount);
       this.keyFrames=keyFrames;
        //using LinkedList, because of implemented remove()
    }
    
    @Override
    public void run(long delta){
   	 for (Frame keyFrame : keyFrames) {
          if(keyFrame.getWhenStarts()<=actTime){
              keyFrame.whatToDo();
              doneKeyFrames.add(keyFrame);
          }
      }
      for (Frame keyFrame : doneKeyFrames) {
          keyFrames.remove(keyFrame);
      }               
    }
    
	@Override
	public void finish() {
		for (Frame keyFrame : keyFrames) {
			keyFrame.whatToDo();
			doneKeyFrames.add(keyFrame);
		}
		for (Frame keyFrame : doneKeyFrames) {
			keyFrames.remove(keyFrame);
		}

		for (Frame keyFrame : doneKeyFrames) { // refill frames to do
			keyFrames.add(keyFrame);
		}
		doneKeyFrames.clear();
	}
    
    @Override
    public void start(){ //(re)start
   	super.start();
    }  
}
