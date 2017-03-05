package net.sf.ardengine.animations;

/**
 * This class stores commands, which should FrameAnimation do in given time
 * by overriding method whatToDo().
 */
public class Frame {
    /**
     * Time from start of Frame animation, at which animation calls whatToDo() method.
     */
    private long when;
      /**
      * @param when Time from start of Frame animation, at which animation calls whatToDo() method.
      */
    public Frame(long when){
        this.when=when;
    }    
      /**
      * Method to override by commands, which will be done in given time.
      */
    public void whatToDo(){
        System.out.println(when+": Nothing to do!");
    }
    
    /**
     * @return Time from start of Frame animation, at which animation calls whatToDo() method.
     */
    public  long getWhenStarts(){
        return when;
    }   
}
