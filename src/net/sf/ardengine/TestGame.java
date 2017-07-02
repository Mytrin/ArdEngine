package net.sf.ardengine;

import javafx.scene.paint.Color;
import net.sf.ardengine.collisions.CollisionPolygon;
import net.sf.ardengine.events.AEvent;
import net.sf.ardengine.events.EventType;
import net.sf.ardengine.events.IEvent;
import net.sf.ardengine.events.IListener;
import net.sf.ardengine.input.TextBox;
import net.sf.ardengine.shapes.Circle;
import net.sf.ardengine.shapes.Line;
import net.sf.ardengine.shapes.Polygon;
import net.sf.ardengine.shapes.Rectangle;
import net.sf.ardengine.text.Text;

/**
 * Simple class implementing game for testing purposes
 * Also used as default IGame param at Core
 */
class TestGame implements IGame{

    @Override
    public void gameInit() {
        addDrawable(getInitBackground());
        //Sprite test
        /*Sprite pngTest = new Sprite("test/lena.bmp");
        pngTest.setZ(0);

		addDrawable(pngTest);
		Sprite jpgTest = new Sprite("test/test.jpg");
		jpgTest.setX(200);
        jpgTest.setZ(-10);
		jpgTest.setScale(2);
        jpgTest.setColor(Color.BLUE);
		jpgTest.setAngle(90);
		addDrawable(jpgTest);
        //pngTest.setZ(10);*/

        //SpriteSheet test
        SpriteSheet spriteSheet = new SpriteSheet("test/spritesheet4.png", 100, 50);
        spriteSheet.setX(200);
        spriteSheet.setY(300);
        addDrawable(spriteSheet);

        //Text test
		Text test = new Text("Lorem Ipsum\n" +
                "Dolor Sit", FontManager.getFont("default"));
        test.setWrapWidth(10);
        test.setX(80);
        test.setY(10);
        //addDrawable(test);

        Text specialChars = new Text("ÁáÉéÍíÓóÚúŮůČčŘřŽžÝýÉéěňťď", FontManager.getFont("default"));
        specialChars.setX(400);
        specialChars.setY(100);
        addDrawable(specialChars);

		/*Group groupTest = new Group();
		groupTest.setX(400);
		groupTest.setY(300);
		groupTest.addChild(test);
		groupTest.addChild(specialChars);
        groupTest.setAngle(90);
        addDrawable(groupTest);*/

        //Shapes test
        Circle circle = new Circle(120, 120, 20);
        circle.setStaticX(100);
        circle.setStaticY(100);
        circle.setStatic(true);
        circle.setColor(Color.RED);
        addDrawable(circle);
        Rectangle rect = new Rectangle(150, 100, 30, 20);
        rect.setColor(Color.GREEN);
        addDrawable(rect);
        Polygon polygon = new Polygon(185, 100, new float[]{0, 0, 30, 0, 15, 30});
        polygon.setColor(Color.BLUE);
        polygon.setAngle(180);
        addDrawable(polygon);
        Line line = new Line(205, 100, new float[]{0, 0, 30, 0, 15, 30});
        line.setColor(Color.DARKBLUE);
        line.setStrokeWidth(2.5f);
        addDrawable(line);

        //Animation test
        /*LinkedList<Frame> frames = new LinkedList<>();
        frames.add(new Frame(5000){
            @Override
            public void whatToDo() {
                System.out.println("5s");
            }
        });
        FrameAnimation fa = new FrameAnimation(10000, frames, 1);
        fa.start();*/

        TextBox testBox = new TextBox(175, 32);
        testBox.setX(100);
        testBox.setY(400);
        testBox.registerListener(new IListener() {
            @Override
            public EventType getType() {
                return EventType.CUSTOM;
            }

            @Override
            public void process(IEvent event) {
                if(event instanceof AEvent){
                    String customType = ((AEvent) event).getCustomEventType();
                    if(customType!=null && customType.equals(TextBox.TEXT_CHANGE_EVENT_TYPE)){
                        System.out.println(((TextBox.TextChangeEvent)event).newValue);
                    }
                }
            }
        });
        testBox.registerListener(new IListener() {
            @Override
            public EventType getType() {
                return EventType.MOUSE_CLICKED;
            }

            @Override
            public void process(IEvent event) {
                System.out.println("Running away!");
                testBox.setX(testBox.getX()+ 200);
            }
        });
        addDrawable(testBox);

        Text version = new Text("Version: "+Core.VERSION, FontManager.getFont("default"));
        version.setX(620);
        version.setY(570);
        version.setCollideable(true);
        version.getCollisions().add(new CollisionPolygon(new float[]{0,0, 15,0, 15,15, 0,15}, version));
        version.registerListener(new IListener() {
            @Override
            public EventType getType() {
                return EventType.MOUSE_CLICKED;
            }

            @Override
            public void process(IEvent event) {
                System.out.println(Core.VERSION);
            }
        });
        addDrawable(version);
    }

    @Override
    public void gameRun() {}

    @Override
    public void gameCleanUp() {}
}