package net.sf.ardengine;

import javafx.scene.paint.Color;
import net.sf.ardengine.renderer.IDrawableImpl;
import net.sf.ardengine.renderer.ISpriteImpl;

/**
 * Common methods for Sprite-like classes(Sprite, SpriteSheet, SpriteAtlas?)
 */
public abstract class ASprite implements IDrawable {

    //REAL location, used for rendering and collisions
    protected float x = 0;
    protected float y = 0;
    protected float z = 0;

    //LOCKED location, so it looks like that drawable is stucked on one place whenever camera moves
    protected float staticX = 0;
    protected float staticY = 0;
    protected boolean isStaticLocked = false;

    //GROUP OFFSET location
    protected float layoutX = 0;
    protected float layoutY = 0;
    protected float layoutOffsetX = 0;
    protected float layoutOffsetY = 0;

    /** Opacity of Sprite image */
    private float opacity = 1.0f;
    /** Scale of Sprite image */
    private float scale = 1.0f;
    /** Rotation of Sprite image */
    private float rotated = 0;
    /**Coloring of Sprite imga*/
    private Color color = Color.WHITE;

    /**Used by renderer to store object with additional requirements*/
    protected ISpriteImpl implementation;

    @Override
    public void draw(){
        implementation.draw();
    }

    @Override
    public float getX() {
        if(!isStaticLocked) return x+layoutOffsetX;
        return staticX;
    }

    @Override
    public void setX(float newX) {
        x = newX;
    }

    @Override
    public float getY() {
        if(!isStaticLocked) return y+layoutOffsetY;
        return staticY;
    }

    @Override
    public void setY(float newY) {
        y = newY;
    }

    @Override
    public float getZ() {
        return z;
    }

    @Override
    public void setZ(float newZ) {
        z = newZ;
        Core.childrenZChanged(this);
    }

    @Override
    public float getStaticX() {
        return staticX;
    }

    @Override
    public void setStaticX(float newStaticX) {
        staticX = newStaticX;
    }

    @Override
    public float getStaticY() {
        return staticY;
    }

    @Override
    public void setStaticY(float newStaticY) {
        staticY = newStaticY;
    }

    @Override
    public boolean isStatic() {
        return isStaticLocked;
    }

    @Override
    public void setStatic(boolean staticLock) {
        isStaticLocked = staticLock;
    }


    @Override
    public void setLayoutX(float layoutX) {
        this.layoutX = layoutX;
    }

    @Override
    public float getLayoutX() {
        return layoutX;
    }

    @Override
    public void setLayoutY(float layoutY) {
        this.layoutY = layoutY;
    }

    @Override
    public float getLayoutY() {
        return layoutY;
    }

    @Override
    public void setLayoutOffsetCoords(float newLayoutOffsetX, float newLayoutOffsetY) {
        this.layoutOffsetX = newLayoutOffsetX;
        this.layoutOffsetY = newLayoutOffsetY;
    }

    @Override
    public float getLayoutOffsetX() {
        return layoutOffsetX;
    }

    @Override
    public float getLayoutOffsetY() {
        return layoutOffsetY;
    }

    @Override
    public IDrawableImpl getImplementation() {
        return implementation;
    }

    @Override
    public float getAngle() {
        return rotated;
    }

    @Override
    public void setAngle(float angle) {
        if (angle < 360 && angle > -360) {
            rotated = angle;
        } else {
            rotated = angle%360;
        }
        implementation.update();
    }

    @Override
    public void setScale(float newScale) {
        scale = newScale;
        implementation.update();
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public void setOpacity(float newOpacity) {
        opacity = newOpacity;
        implementation.update();
    }

    @Override
    public float getOpacity() {
        return opacity;
    }

    @Override
    public void setColor(Color color){
        this.color = color;
        implementation.update();
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public float getHeight(){
        return implementation.getHeight();
    }

    @Override
    public float getWidth(){
        return implementation.getWidth();
    }

}