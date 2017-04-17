package net.sf.ardengine.collisions;

import net.sf.ardengine.Core;
import net.sf.ardengine.Node;

/**
 * Collision shape - Circle
 */
public class CollisionCircle extends ACollisionShape {

      /**X coord of Circle center in shape*/
      private float centerX;
      /**Y coord of Circle center in shape */
      private float centerY;
      /**X coord of Circle center in world */
      private float actCenterX;
      /**Y coord of Circle center in world */
      private float actCenterY;
      /**Radius of this circle */
      private float radius;
      /**Actual X coord of node*/
      private float nodeX;
      /**Actual Y coord of node */
      private float nodeY;

    /**Quality of circle rendered in DEBUG mode*/
    private static final int SMOOTHNESS = 36;
    /**Coordinates cached for debug rendering*/
    private float[] renderCoords;

    private Node targetNode;

      /**
       * @param centerX X coord of center in node(like node's X would be [0;0])
       * @param centerY Y coord of center in node(like node's Y would be [0;0])
       * @param radius Radius of circle  
       * @param e Node, for which is shape created 
       */
      public CollisionCircle(float centerX, float centerY, float radius, Node e){
        this.centerX=centerX;
        this.centerY=centerY;
        this.actCenterX=centerX;
        this.actCenterY=centerY;
        this.radius=radius;
        this.targetNode = e;

        updateProperties();
      }
  
      @Override
      public boolean isColliding(ACollisionShape cs){
        
        if(cs instanceof CollisionPolygon){
            CollisionPolygon cp=(CollisionPolygon)cs;
            
            float[] aktCoords=cp.getActCoords();
            
            for(int i=0; i<aktCoords.length-1;i+=2){
               
              float ax=aktCoords[i];
              float ay=aktCoords[i+1];
              float bx;
              float by;
              if(i+2<aktCoords.length-1){
                bx=aktCoords[i+2];
                by=aktCoords[i+3];
              }else{
                bx=aktCoords[0];
                by=aktCoords[1];
              }
             
              if(!withScale){
                  if(distToSegment(ax, ay, bx, by, getCenterX(), getCenterY())<radius){
                  return true;
                  }  
              }else{
                  if(distToSegment(ax, ay, bx, by, getCenterX(), getCenterY())<radius*aktScale){
                  return true;
                  }
              }
           }
           return false;
            
        }else if(cs instanceof CollisionCircle){
            CollisionCircle cc=(CollisionCircle)cs;
            
            float abx=cc.getCenterX()-getCenterX();
            float aby=cc.getCenterY()-getCenterY();
            
            float dst= (float)(Math.sqrt(Math.pow(abx, 2)+Math.pow(aby, 2)));
            
            float myRadius=getRadius();
            if(withScale){
                myRadius*=aktScale;
            }            
            float hisRadius=cc.getRadius();
            if(cc.withScale){
                hisRadius*=cc.aktScale;
            }
            
             if(dst>(hisRadius+myRadius)){
                return false;
            }else{
                return true;
            }
        }

        return false;
    }
      
      @Override
     public void updateProperties(){
        nodeX=targetNode.getX();
        nodeY=targetNode.getY();
        aktScale=targetNode.getScale();
        float nodeCenterX=targetNode.getX()+targetNode.getWidth()/2;
        float nodeCenterY=targetNode.getY()+targetNode.getHeight()/2;
        float angleRadians;
        if(!(targetNode.getAngle()<0)){
            angleRadians =(float)((targetNode.getAngle()) * Math.PI / 180);
        }else{
            angleRadians =(float)((360+targetNode.getAngle()) * Math.PI / 180);
        }       
          
            actCenterX=(float)(centerX+(nodeX+centerX - nodeCenterX) * Math.cos(angleRadians) + (nodeY+centerY - nodeCenterY) * Math.sin(angleRadians));
            actCenterY=(float)(centerY-(nodeX+centerX - nodeCenterX) * Math.sin(angleRadians) + (nodeY+centerY - nodeCenterY) * Math.cos(angleRadians));
          generateRenderCoords();
      }

     public float getCenterX(){
         return actCenterX+nodeX;
     }
     
     public float getCenterY(){
         return actCenterY+nodeY;
     }
     
     public float getRadius(){
         return radius;
     }
     
      @Override
     public float[] getActCoords(){
       return new float[]{getCenterX(),getCenterY()};
     }

    //DEBUG

    /**
     * Creates coordinates for debug rendering
     */
    private void generateRenderCoords(){
        if(renderCoords == null) renderCoords = new float[(SMOOTHNESS+1)*2];

        for(int i = 0; i <= SMOOTHNESS; i++){ //36 = smoothness of circle
            double angle = Math.PI * 2 * i / 18;
            renderCoords[i*2]=(float)(Math.cos(angle)*radius);
            renderCoords[i*2+1]=(float)(Math.sin(angle)*radius);
        }

        for(int i=0; i < renderCoords.length; i+=2){
            renderCoords[i] -= Core.cameraX;
            renderCoords[i+1] -= Core.cameraY;
        }
    }

    @Override
    public float[] getLineCoordinates() {
        return renderCoords;
    }
}