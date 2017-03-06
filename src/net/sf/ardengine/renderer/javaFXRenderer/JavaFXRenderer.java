package net.sf.ardengine.renderer.javaFXRenderer;

import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.ImageCursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import net.sf.ardengine.Core;
import net.sf.ardengine.IDrawable;
import net.sf.ardengine.Sprite;
import net.sf.ardengine.SpriteSheet;
import net.sf.ardengine.renderer.javaFXRenderer.shapes.JavaFXCircleImpl;
import net.sf.ardengine.renderer.javaFXRenderer.shapes.JavaFXLineImpl;
import net.sf.ardengine.renderer.javaFXRenderer.shapes.JavaFXPolygonImpl;
import net.sf.ardengine.renderer.javaFXRenderer.shapes.JavaFXRectangleImpl;
import net.sf.ardengine.renderer.*;
import net.sf.ardengine.renderer.util.NotSupported;
import net.sf.ardengine.shapes.*;
import net.sf.ardengine.text.IFont;
import net.sf.ardengine.text.ITextImpl;
import net.sf.ardengine.text.Text;

/**
 * EMERGENCY RENDERER IMPLEMENTATION
 * @author mytrin
 */
public class JavaFXRenderer extends Application implements IRenderer{

	private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
	
	private static boolean fxThreadRunning = false;
	
	private Stage stage;
    private final Group children = new Group();
	private Scene scene;
	
	private static double baseWidth = 100;
	private static double baseHeight = 100;
	private static double deltaX = 50;
	private static double deltaY = 50;
	private static double scaleX = 1;
	private static double scaleY = 1;

    private long delta;

	//FPS
	private static final float TIME_STEP = 0.0166f; //JavaFX has 60FPS
	private Timeline watcher = new Timeline(new KeyFrame(Duration.seconds(TIME_STEP), new EventHandler<ActionEvent>(){
		public void handle(ActionEvent event){
			if(frameLock) return;
		  	 
 	  			frameLock = true;
      
 	  			//FPS tracking
 	  			currentFPScount++;
 	  			delta = System.currentTimeMillis() - lastUpdateTime;
 	  			testFPStime += delta;
 	  			if(testFPStime > 1000){
 	  				testFPStime = 0;
 	  				averageFps = currentFPScount;
 	  				currentFPScount = 0;
 	  			}
 	  			lastUpdateTime = System.currentTimeMillis();
		
 	  			Core.run(delta);
 	  			
 	  			frameLock = false;
		}
	}));
	
	public static final int FPS = 60; //Javafx default
	private long lastUpdateTime = 0;
	private long testFPStime = 0;
	private int averageFps = 0;
	private int currentFPScount = 0;
	
	private boolean frameLock = false;

    //INPUT
    JavaFXInputReceiver inputReceiver;
	/**
	 * Creates renderer instance and starts fx thread.
	 * 
	 * @param width Width of new window
	 * @param height Height of new window
	 */
	public static void createRenderer(int width, int height){
		if(!fxThreadRunning){
			baseWidth = width;
			baseHeight = height;
			launch(JavaFXRenderer.class); // ->> start(Stage stage)
		}
	}

	@Override
	public void start(Stage stage) throws Exception {
		try{
			fxThreadRunning = true;
			this.stage = stage;

			//Scaling the content when window resized
			scene = new Scene(children, Color.BLACK);
			//Show window
			stage.setScene(scene);
			stage.setTitle(IRenderer.DEFAULT_WINDOW_TITLE+" - JavaFX");
			stage.setHeight(baseHeight);
			stage.setWidth(baseWidth);
			stage.setOnCloseRequest(close -> Core.destroy());
			stage.show();

            inputReceiver = new JavaFXInputReceiver(scene);

			//Start cycle
			watcher.setCycleCount(Timeline.INDEFINITE);
			watcher.play();
		}catch(Exception e){
			//Something failed at initialization
			//There is no renderer with lower priority than this, so CRASH
			Core.notifyWhenRendererReady(this, RendererState.CRASH);
			return;
		}

		//Continue in game init
		LOGGER.info("JavaFX renderer started!");
		Core.notifyWhenRendererReady(this, RendererState.READY);
	}

	@Override
	public void render(List<IDrawable> drawables) {
		updateSceneScale();
		
		for(IDrawable drawable: drawables){
                drawable.draw();
		}
	}

	@Override
	public void afterRender() {}

	@Override
	public void cleanUp() {
		watcher.stop();
		//JavaFX cleanups automatically
	}

    @Override
    public long getDelta() {
        return delta;
    }

    private void updateSceneScale(){

		double newScaleX = stage.getWidth()/ baseWidth;
		double newScaleY = stage.getHeight()/ baseHeight;
		if(newScaleX != scaleX){
			scaleX = newScaleX;
			children.setScaleX(scaleX);
		}

		if(newScaleY != scaleY){
			scaleY = newScaleY;
			children.setScaleY(scaleY);
		}

		//Scaled children are centered around their center
		double centerX = children.getLayoutBounds().getMinX()+children.getLayoutBounds().getWidth()/2.0;
		double centerY = children.getLayoutBounds().getMinY()+children.getLayoutBounds().getHeight()/2.0;

		double newDeltaX = centerX*children.getScaleX() - centerX;
		double newDeltaY = centerY*children.getScaleY() - centerY;

		if(newDeltaX != deltaX){
			deltaX = newDeltaX;
			children.setLayoutX(deltaX);
		}

		if(newDeltaY != deltaY){
			deltaY = newDeltaY;
			children.setLayoutY(deltaY);
		}
	}
	
	//WINDOW GETTER AND SETTERS
	@Override
	public int getWindowWidth() {
		if(stage != null){
			return (int)stage.getWidth();
		}else{
			return 0;
		}
	}

    @Override
    public int getBaseWindowWidth() {
        return (int)baseWidth;
    }

    @Override
	public void setWindowWidth(int width) {
		if(stage != null){
			stage.setWidth(width);
		}
		baseWidth = width;
	}

	@Override
	public int getWindowHeight() {
		if(stage != null){
			return (int)stage.getHeight();
		}else{
			return 0;
		}
	}

	@Override
	public int getBaseWindowHeight() {
		return (int)baseHeight;
	}

	@Override
	public void setWindowHeight(int height) {
		if(stage != null){
			stage.setHeight(height);
		}
		baseHeight = height;
	}
	
	@Override
	public void setWindowSize(int width, int height) {
		if(stage != null){
			stage.setWidth(width);
			stage.setHeight(height);
		}
		baseWidth = width;
		baseHeight = height;
	}
	
	@Override
	public String getWindowTitle() {
		if(stage != null){
			return stage.getTitle();
		}
		return IRenderer.DEFAULT_WINDOW_TITLE;
	}
	
	@Override
	public void setWindowTitle(String title) {
		if(stage != null){
			stage.setTitle(title);
		}
	}
	
	@Override
	public boolean isWindowResizeable() {
		if(stage != null){
			return stage.isResizable();
		}
		return true; //default value
	}
	
	@Override
	public void setWindowResizeable(boolean resize) {
		if(stage != null){
			stage.setResizable(resize);
		}
	}
	
	@Override
	public int getAverageFPS() {
		return averageFps;
	}
	
	@Override
	public void setDesiredFPS(int desiredFPS) {
		LOGGER.warning("JavaFX renderer cannot use other fps than 60!");
	}
	
	@Override
	public void childAdded(IDrawable child, int index) {
		IDrawableImpl childNode = child.getImplementation();

		if(childNode != null){
            LOGGER.fine(childNode.toString());
			children.getChildren().add(index, ((Node)childNode));
		}
	}
	
	@Override
	public void removeChildren(List<IDrawable> children) {
        children.forEach((child)->{
            IDrawableImpl childNode = child.getImplementation();
            if(childNode != null){
                this.children.getChildren().remove((Node)childNode);
            }
        });
	}

	@Override
	public void destroyChildren(List<IDrawable> children) {
        //let JavaFX handle it
	}

    @Override
    public void childMoved(IDrawable child, int index) {
        IDrawableImpl childNode = child.getImplementation();
        this.children.getChildren().remove(childNode);
        this.children.getChildren().add(index, (Node)childNode);
    }

    //DRAWABLE IMPLEMENTATIONS
	public ISpriteImpl createSpriteImplementation(InputStream is, Sprite parentSprite) {
		return new JavaFXSpriteImpl(is, parentSprite);
	}
	
	@Override
	public ISpriteImpl createSpriteImplementation(String url, Sprite parentSprite) {
		return new JavaFXSpriteImpl(url, parentSprite);
	}

    @Override
    public ISpriteSheetImpl createSpriteSheetImplementation(InputStream is, SpriteSheet parentSprite) {
		return new JavaFXSpriteSheetImpl(is, parentSprite);
    }

    @Override
    public ISpriteSheetImpl createSpriteSheetImplementation(String url, SpriteSheet parentSprite) {
		return new JavaFXSpriteSheetImpl(url, parentSprite);
    }

    @Override
    public ISpriteSheetImpl createSpriteSheetImplementation(BufferedImage buf, SpriteSheet parentSprite) {
		return new JavaFXSpriteSheetImpl(buf, parentSprite);
    }
	
	@Override
	public ISpriteImpl createSpriteImplementation(BufferedImage buf, Sprite parentSprite) {
		return new JavaFXSpriteImpl(buf, parentSprite);
	}
	
	@Override
	public ITextImpl createTextImplementation(IFont font, Text parentText) {
		return new JavaFXTextImpl(((JavaFXFont)font).getFxFont(), parentText);
	}
	
	@Override
	public IShapeImpl createShapeImplementation(IShape shape) {
		switch(shape.getType()){
			case CIRCLE: return new JavaFXCircleImpl((Circle)shape);
			case POLYGON: return new JavaFXPolygonImpl((Polygon)shape);
			case RECTANGLE: return new JavaFXRectangleImpl((Rectangle)shape);
			case LINES: return new JavaFXLineImpl((Line)shape);
		}
		return null;
	}

	@Override
	public IGroupImpl createGroupImplementation(net.sf.ardengine.Group parentGroup) {
		return new JavaFXGroupImpl(parentGroup);
	}

	//FONTS
	public IFont loadFont(String fontName, int size, char[] specialCharacters) {
		return new JavaFXFont(fontName, size);
	}
	
	@Override
	public IFont loadFont(InputStream font, int size, char[] specialCharacters) {
		return new JavaFXFont(font, size);
	}


    @Override @NotSupported
	public void renderCollisions(List<net.sf.ardengine.Node> collideables){
		//never ever, too much operations for so few fun
	}

    @Override
    public float getMouseX() {
        return inputReceiver.getMouseX();
    }

    @Override
    public float getMouseY() {
        return inputReceiver.getMouseY();
    }

	@Override
	public void setMouseImage(InputStream is) {
        Image image = new Image(is);
        scene.setCursor(new ImageCursor(image));
    }

	@Override
	public void setMouseImage(String url) {
        Image image = new Image(url);
        scene.setCursor(new ImageCursor(image));
	}
}