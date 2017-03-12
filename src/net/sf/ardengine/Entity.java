package net.sf.ardengine;

import javafx.scene.paint.Color;
import net.sf.ardengine.renderer.IDrawableImpl;

/**
 * Entity wraps Sprite to provide same functionality as Node.
 */
public class Entity extends Node {
    /** Image of this Entity */
    protected ASprite sprite;

    /**
     * @param sprite image of this entity
     * @param x X coord
     * @param y Y coord
     */
    public Entity(ASprite sprite, float x, float y) {
        this(sprite, x, y, 0, 0);
    }

    /**
     * @param sprite image of this entity
     * @param x X coord
     * @param layoutX X coord in Group
     * @param y  coord
     * @param layoutY Y coord in Group
     */
    public Entity(ASprite sprite, float x, float y, float layoutX, float layoutY) {
        this.sprite = sprite;
        setLayoutX(layoutX);
        setLayoutY(layoutY);
        setX(x);
        setY(y);
    }

    @Override
    public void draw() {
        sprite.draw();
    }

    @Override
    public IDrawableImpl getImplementation() {
        return sprite.getImplementation();
    }

    /**
     * @return Sprite of this entity
     */
    public ASprite getSprite() {
        return sprite;
    }

    /**
     * Sets the sprite of this entity
     * @param s new image of this entity
     */
    public void setSprite(ASprite s) {
        sprite = s;
    }

    @Override
    public void setLayoutOffsetCoords(float newLayoutOffsetX, float newLayoutOffsetY) {
        super.setLayoutOffsetCoords(newLayoutOffsetX, newLayoutOffsetY);
        sprite.setLayoutOffsetCoords(newLayoutOffsetX, newLayoutOffsetY);
    }

    @Override
    public void setLayoutX(float layoutX) {
        super.setLayoutX(layoutX);
        sprite.setLayoutX(layoutX);
    }

    @Override
    public void setLayoutY(float layoutX) {
        super.setLayoutY(layoutX);
        sprite.setLayoutY(layoutX);
    }

    @Override
    public void setX(float newX) {
        super.setX(newX);
        sprite.setX(newX);
    }

    @Override
    public void setY(float newY) {
        super.setY(newY);
        sprite.setY(newY);
    }

    @Override
    public void setStaticX(float newStaticX) {
        super.setStaticX(newStaticX);
        sprite.setStaticX(newStaticX);
    }

    @Override
    public void setStaticY(float newStaticY) {
        super.setStaticY(newStaticY);
        sprite.setStaticY(newStaticY);
    }

    @Override
    public float getWidth() {
        return sprite.getWidth();
    }

    @Override
    public float getHeight() {
        return sprite.getHeight();
    }

    @Override
    public void setOpacity(float newOpacity) {
        super.setOpacity(newOpacity);
        sprite.setOpacity(newOpacity);
    }

    @Override
    public void setScale(float newScale) {
        super.setScale(newScale);
        sprite.setScale(newScale);
    }

    @Override
    public void setAngle(float newAngle) {
        super.setAngle(newAngle);
        sprite.setAngle(newAngle);
    }

    @Override
    public void setColor(Color color) {
        super.setColor(color);
        sprite.setColor(color);
    }
}