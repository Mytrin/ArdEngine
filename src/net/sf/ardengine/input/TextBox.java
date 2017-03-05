package net.sf.ardengine.input;


import javafx.scene.paint.Color;
import net.sf.ardengine.FontManager;
import net.sf.ardengine.Group;
import net.sf.ardengine.Node;
import net.sf.ardengine.collisions.CollisionPolygon;
import net.sf.ardengine.events.AEvent;
import net.sf.ardengine.events.EventType;
import net.sf.ardengine.events.IEvent;
import net.sf.ardengine.events.IListener;
import net.sf.ardengine.shapes.Line;
import net.sf.ardengine.shapes.Rectangle;
import net.sf.ardengine.text.IFont;
import net.sf.ardengine.text.Text;

/**
 * High level input component
 */
public class TextBox extends Group implements IKeyTyper{

    /**Custom type of events invoked, when content changes*/
    public static final String TEXT_CHANGE_EVENT_TYPE = "text-changed";

    /**Width of TextBox background*/
    protected float width;
    /**Height of TextBox background*/
    protected float height;

    /**Text rendering*/
    private Text text;
    /**Background rectangle*/
    private Rectangle background;
    /**Lines surrounding background*/
    private Line border;

    /**Maximal number of text characters*/
    private int maxCharacters = -1;

    /**True, if textbox accepts input*/
    private boolean isEditable = true;

    /**
     * Generates empty TextBox using default font with wrapping width equal to width-1
     * @param width width of TextBox background
     * @param height height of TextBox background
     */
    public TextBox(float width, float height){
        this("", FontManager.getFont(FontManager.DEFAULT_KEY), width, height, Math.round(width-1));
    }

    /**
     * Generates empty TextBox with wrapping width equal to width-1
     * @param font used font
     * @param width width of TextBox background
     * @param height height of TextBox background
     */
    public TextBox(IFont font, float width, float height){
        this("", font, width, height, Math.round(width-1));
    }

    /**
     * Generates TextBox with wrapping width equal to width-1
     * @param text Text content of TextBox
     * @param font used font
     * @param width width of TextBox background
     * @param height height of TextBox background
     */
    public TextBox(String text, IFont font, float width, float height){
        this(text, font, width, height, Math.round(width-1));
    }

    /**
     * @param text Text content of TextBox
     * @param font used font
     * @param width width of TextBox background
     * @param height height of TextBox background
     * @param wrappingWidth maximal width of textbox row
     */
    public TextBox(String text, IFont font, float width, float height, int wrappingWidth){
        this.width = width;
        this.height = height;
        this.text = new Text(text, font, wrappingWidth);
            this.text.setLayoutX(1);
            this.text.setLayoutY(1);

        rebuildGraphics();
        background.setColor(Color.WHITE);
        border.setStrokeWidth(1.5f);

        setCollideable(true);
        getCollisions().add(new CollisionPolygon(new float[]{0, 0, width, 0, width, height, 0, height, 0, 0}, this));

        registerListener(new IListener() {
            @Override
            public EventType getType() {
                return EventType.MOUSE_MOVED;
            }

            @Override
            public void process(IEvent event) {
                border.setColor(Color.BLUE);
            }
        });

        registerListener(new IListener() {
            @Override
            public EventType getType() {
                return EventType.MOUSE_OUT;
            }

            @Override
            public void process(IEvent event) {
                border.setColor(Color.BLACK);
            }
        });

        registerListener(new IListener() {
            @Override
            public EventType getType() {
                return EventType.MOUSE_CLICKED;
            }

            @Override
            public void process(IEvent event) {
                InputManager.getInstance().setActiveKeyTyper(TextBox.this);
            }
        });
    }

    /**
     * Creates border and background to fit actual size
     */
    private final void rebuildGraphics(){
        Rectangle newBackground =  new Rectangle(0, 0, width, height);
        Line newBorder = new Line(0, 0, new float[]{-1, -1, width+1, -1, width+1, height+1, -1, height+1, -1,-1});

        if(background != null && border != null){
            newBackground.setOpacity(background.getOpacity());
            newBackground.setColor(background.getColor());
            newBorder.setStrokeWidth(border.getStrokeWidth());
            newBorder.setOpacity(border.getOpacity());
            newBorder.setColor(border.getColor());
        }
        background = newBackground;
        border = newBorder;

        clearChildren();
        addChildren(new Node[]{background, border, this.text});
    }

    /**
     * Checks current count of characters and cuts excessive ones
     */
    private void checkMaxCharacters(){
        if(maxCharacters == -1) return;

        String content = text.getText();

        int difference = content.length() - maxCharacters;
        if(difference > 0){
            String newValue = content.substring(0, content.length()-difference);
            text.setText(newValue);

            invokeEvent(new TextChangeEvent(content, newValue));
        }
    }

    @Override
    public void keyTyped(char key) {
        if(!isEditable) return;

        String oldText = text.getText();

        text.setText(oldText+key);

        if(text.getHeight() > height){
            text.setText(oldText);
        }else{
            invokeEvent(new TextChangeEvent(oldText, text.getText()));
        }

        checkMaxCharacters();
    }

    @Override
    public void backspacePressed() {
        String oldText = text.getText();
        if(oldText.length() > 0){
            text.setText(oldText.substring(0, oldText.length()-1));
            invokeEvent(new TextChangeEvent(oldText, text.getText()));
        }
    }

    @Override
    public void enterPressed() {
        keyTyped('\n');
    }

    /**
     * @param text new content of textbox
     */
    public void setText(String text) {
        String oldText = this.text.getText();
        this.text.setText(text);

        invokeEvent(new TextChangeEvent(oldText, text));

        checkMaxCharacters();
    }

    /**
     * @return content of textbox
     */
    public String getText() {
        return text.getText();
    }

    /**
     * @param font font used for text rendering
     */
    public void setFont(IFont font) {
        text.setFont(font);
    }

    /**
     * @param wrapWidth maximal width of textbox line
     */
    public void setWrappingWidth(int wrapWidth) {
        text.setWrapWidth(wrapWidth);
    }

    /**
     * @param paddingX text X offset from upper left border corner
     * @param paddingY text Y offset from upper left border corner
     */
    public void setTextPadding(float paddingX, float paddingY){
        text.setLayoutX(paddingX);
        text.setLayoutY(paddingY);
    }

    /**
     * -1 means INFINITE
     * @param maxCharacters maximal number of characters of textbox content
     */
    public void setMaxCharacters(int maxCharacters) {
        this.maxCharacters = maxCharacters;
        checkMaxCharacters();
    }

    /**
     * @param color color of background rectangle
     */
    public void setBackgroundColor(Color color) {
        background.setColor(color);
    }

    /**
     *
     * @param opacity opacity of background rectangle
     */
    public void setBackgroundOpacity(float opacity) {
        background.setOpacity(opacity);
    }

    /**
     * @param width strokeWidth of surrounding lines
     */
    public void setBorderWidth(float width){
        border.setStrokeWidth(width);
    }

    /**
     * @param color color of surrounding lines
     */
    public void setBorderColor(Color color) {
        border.setColor(color);
    }

    /**
     * @param opacity opacity of surrounding lines
     */
    public void setBorderOpacity(float opacity) {
        border.setOpacity(opacity);
    }

    /**
     * @param color color of surrounding text
     */
    public void setTextColor(Color color){
        text.setColor(color);
    }

    /**
     * @param width Width of TextBox background
     */
    public void setWidth(float width) {
        this.width = width;
        rebuildGraphics();
    }

    /**
     * @param height Height of TextBox background
     */
    public void setHeight(float height) {
        this.height = height;
        rebuildGraphics();
    }

    /**
     * @return True, if textbox accepts input
     */
    public boolean isEditable() {
        return isEditable;
    }

    /**
     * @param editable True, if textbox accepts input
     */
    public void setEditable(boolean editable) {
        isEditable = editable;
    }

    @Override
    public float getWidth() {
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    /**
     * Custom TextBox event invoked when text content changed
     */
    public class TextChangeEvent extends AEvent {
        /**Old text content*/
        public final String oldValue;
        /**New text content*/
        public final String newValue;

        /**
         * @param oldValue Old text content
         * @param newValue New text content
         */
        public TextChangeEvent(String oldValue, String newValue){
            super(EventType.CUSTOM);
            this.oldValue = oldValue;
            this.newValue = newValue;
        }

        @Override
        public String getCustomEventType() {
            return TEXT_CHANGE_EVENT_TYPE;
        }
    }
}