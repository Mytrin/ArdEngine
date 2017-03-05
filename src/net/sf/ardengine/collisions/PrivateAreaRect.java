package net.sf.ardengine.collisions;

import javafx.scene.paint.Color;
import net.sf.ardengine.Node;
import net.sf.ardengine.renderer.util.IRenderableCollision;

/**
 * This class also  is added to Node when it is marked as collideable.
 * If two private areas collide, engine tests node's collision shapes to confirm collision.
 */
public class PrivateAreaRect implements IRenderableCollision {
    /**Starting rectangle point X coord*/
    private float startX;
    /**Starting rectangle point Y coord*/
    private float startY;
    /**Ending rectangle point X coord*/
    private float endX;
    /**Ending rectangle point Y coord*/
    private float endY;
    /**Precalculated X coord of rectangle's center*/
    private float centerX;
    /**Precalculated Y coord of rectangle's center*/
    private float centerY;
    /**Actual rect's height */
    private float actHeight;
    /**Actual rect's width */
    private float actWidth;
    /**Indicator if auto detect node scale*/
    private boolean withScale=false;
    /**Variable, where is stored actual node scale*/
    private float aktScale=1;

    private final Node parentNode;
    
    /**
     * @param parentNode Node, which is this PrivateArea tracking
     */
    public PrivateAreaRect(Node parentNode){
        this.parentNode = parentNode;
        startX=parentNode.getX();
        startY=parentNode.getY();
        actWidth=parentNode.getWidth();
        actHeight=parentNode.getHeight();
        endX=startX+actWidth;
        endY=startY+actHeight;
        centerX= startX + actWidth/2;
        centerY= startY + actHeight/2;
    }

    /**
     * @return Rectangle' smallest X
     */
    public float getX(){
     if(!withScale){
        return (centerX - actWidth/2);
     }else{
         return (centerX - actWidth/2*aktScale);
     }
    }

    /**
     * @return Rectangle's largest right X
     */
    public float getX2(){
     if(!withScale){
        return (centerX + actWidth/2);
     }else{
         return centerX + actWidth/2*aktScale;
     }
    }

    /**
     * @return Rectangle's smallest Y
     */
    public float getY(){
     if(!withScale){
        return (centerY - actHeight/2);
     }else{
         return centerY - actHeight/2*aktScale;
     }
    }

    /**
     * @return Rectangle's largest Y
     */
    public float getY2(){
      if(!withScale){
        return (centerY + actHeight/2);
      }else{
         return centerY + actHeight/2*aktScale;
     }
    }

    /**
     * @param he privateArea of tested node
     * @return true if they intersect 
     */
    public boolean intersects(PrivateAreaRect he){
        if(he!=this){
           return !(getX() > he.getX2() || getX2() < he.getX() ||  getY() > he.getY2() || getY2() < he.getY());
        }else{
            return false;
        }                
    }

    /**
     * Updates privateAreas's coords based on node's coords(called automatically by Node).
     */
    public void updateProperties() {
        double angleRadians;

        startX=parentNode.getX();
        startY=parentNode.getY();
        endX=startX+parentNode.getWidth();
        endY=startY+parentNode.getHeight();

        centerX=parentNode.getX()+parentNode.getWidth()/2;
        centerY=parentNode.getY()+parentNode.getHeight()/2;
        
        if(!(parentNode.getAngle()<0)){
            angleRadians = (parentNode.getAngle()%360) * Math.PI / 180;
        }else{
            angleRadians = (360+parentNode.getAngle()%360) * Math.PI / 180;
        }

        double x1prim = (startX - centerX) * Math.cos(angleRadians) - (startY - centerX) * Math.sin(angleRadians);
        double y1prim = (startX - centerX) * Math.sin(angleRadians) + (startY - centerX) * Math.cos(angleRadians);

        double x12prim = (startX - centerX) * Math.cos(angleRadians) - (endY - centerX) * Math.sin(angleRadians);
        double y12prim = (startX - centerX) * Math.sin(angleRadians) + (endY - centerX) * Math.cos(angleRadians);

        double x2prim = (endX - centerX) * Math.cos(angleRadians) - (endY - centerX) * Math.sin(angleRadians);
        double y2prim = (endX - centerX) * Math.sin(angleRadians) + (endY - centerX) * Math.cos(angleRadians);

        double x21prim = (endX - centerX) * Math.cos(angleRadians) - (startY - centerX) * Math.sin(angleRadians);
        double y21prim = (endX - centerX) * Math.sin(angleRadians) + (startY - centerX) * Math.cos(angleRadians);

        double x = centerX + Math.min(Math.min(x1prim, x2prim), Math.min(x12prim, x21prim));
        double y = centerY + Math.min(Math.min(y1prim, y2prim), Math.min(y12prim, y21prim));

        double x2 = centerX + Math.max(Math.max(x1prim, x2prim), Math.max(x12prim, x21prim));
        double y2 = centerY + Math.max(Math.max(y1prim, y2prim), Math.max(y12prim, y21prim));

        actWidth=(float)(x2-x);
        actHeight=(float)(y2-y);

        aktScale=parentNode.getScale();
    }

     /**Does privateArea count with scale?*/
     public boolean testsWithScale(){
         return withScale;
     }
     /**Should privateArea count with scale?*/
     public void testWithScale(boolean test){
         withScale=test;
     }

    //DEBUG
    @Override
    public Color getLineColor() {
        return Color.GOLD;
    }

    @Override
    public float[] getLineCoordinates() {
        return new float[]{
                startX, startY,
                endX,   startY,
                endX,   endY,
                startX, endY
        };
    }
}