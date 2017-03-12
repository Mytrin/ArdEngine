package net.sf.ardengine.animations;

import javafx.scene.paint.Color;
import net.sf.ardengine.IDrawable;

/**
 * Animation, which changes node color over time.
 */
public class ColorTransition extends Animation {

    private IDrawable target;
    private Color startColor;
    private Color endColor;

    private double redChange;
    private double greenChange;
    private double blueChange;
    private double opacityChange;


    /**
     * @param target The taget Node
     * @param duration Duration of animation
     * @param startColor Starting node opacity
     * @param endColor Final node opacity
     * @param repeatCount Count of animation repeats(-1 = infinite)
     */
    public ColorTransition(long duration, IDrawable target, Color startColor, Color endColor, int repeatCount) {
        super(duration, repeatCount);
        this.target = target;
        this.startColor = startColor;
        this.endColor = endColor;

        this.redChange = endColor.getRed()-startColor.getRed();
        this.greenChange = endColor.getGreen()-startColor.getGreen();
        this.blueChange = endColor.getBlue()-startColor.getBlue();
        this.opacityChange = endColor.getOpacity() - startColor.getOpacity();
    }

    @Override
    public void start() {
        super.start();
        actTime = (long)(checkCurrentState()*stopTime);
    }

    private double checkCurrentState(){
        Color control = target.getColor();

        double state;

        if(Math.abs(redChange) > 0.0){
            state =  (control.getRed() - startColor.getRed())/redChange;
        }else if(Math.abs(greenChange) > 0.0){
            state =  (control.getGreen() - startColor.getGreen())/greenChange;
        }else if(Math.abs(blueChange) > 0.0){
            state =  (control.getBlue() - startColor.getBlue())/blueChange;
        }else{
            state = 1.0;
        }

        return Math.abs(state);
    }

    @Override
    public void run(long l) {
        double state = (double)actTime/(double)stopTime;
        Color newColor = new Color(startColor.getRed()+redChange*state,
                startColor.getGreen()+greenChange*state,
                startColor.getBlue()+blueChange*state,
                startColor.getOpacity()+opacityChange*state);
        target.setColor(newColor);
    }

    @Override
    public void finish() {
        target.setColor(endColor);
    }
}
