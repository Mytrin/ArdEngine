package net.sf.ardengine.renderer.javaFXRenderer;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import net.sf.ardengine.ASprite;
import net.sf.ardengine.Core;
import net.sf.ardengine.renderer.ISpriteImpl;

public class JavaFXSpriteImpl extends ImageView implements ISpriteImpl, IJavaFXGroupable {

	protected ASprite parentSprite;

    private Color cachedColor = Color.WHITE;
	
	public JavaFXSpriteImpl(InputStream is, ASprite parentSprite){
		super(new Image(is));
		this.parentSprite = parentSprite;
	}
	
	public JavaFXSpriteImpl(String url, ASprite parentSprite){
		super("File:"+url);
		this.parentSprite = parentSprite;
	}
	
	public JavaFXSpriteImpl(BufferedImage buf, ASprite parentSprite){
		super(convertBufferedImageToRaster(buf));
		this.parentSprite = parentSprite;
	}

	private static final WritableImage convertBufferedImageToRaster(BufferedImage buf){
		WritableImage wr = new WritableImage(buf.getWidth(), buf.getHeight());
		PixelWriter pw = wr.getPixelWriter();
		for (int x = 0; x < buf.getWidth(); x++) {
			for (int y = 0; y < buf.getHeight(); y++) {
				pw.setArgb(x, y, buf.getRGB(x, y));
			}
		}
		return wr;
	}

	@Override
	public void draw() {
		setLayoutX(parentSprite.getX() - (!parentSprite.isStatic()? Core.cameraX:0));
		setLayoutY(parentSprite.getY() - (!parentSprite.isStatic()? Core.cameraY:0));
	}

	@Override
	public void groupDraw() {
		setLayoutX(parentSprite.getLayoutX());
		setLayoutY(parentSprite.getLayoutY());
	}

	@Override
	public void update() {
		setRotate(parentSprite.getAngle());
		setOpacity(parentSprite.getOpacity());
		setScaleX(parentSprite.getScale());
		setScaleY(parentSprite.getScale());

		if(parentSprite.getColor() != cachedColor){
			setColor(parentSprite.getColor());
		}
	}

	@Override
	public void free() {
		//nothing to do
	}

    private void setColor(Color color){
        if(color != Color.WHITE){
            ColorAdjust monochrome = new ColorAdjust();
            monochrome.setSaturation(-1.0);

            Blend blush = new Blend(
                    BlendMode.MULTIPLY,
                    monochrome,
                    new ColorInput(
                            0,
                            0,
                            getWidth(),
                            getHeight(),
                            color
                    )
            );

            setEffect(blush);

            setCache(true);
        }else{
            setEffect(null);
            setCache(false);
        }

        cachedColor = color;
    }

	@Override
	public float getWidth() {
		return (float)getImage().getWidth();
	}
	
	@Override
	public float getHeight() {
		return (float)getImage().getHeight();
	}

}