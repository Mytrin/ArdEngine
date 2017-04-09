package net.sf.ardengine.input.console;

import javafx.scene.paint.Color;
import net.sf.ardengine.Core;
import net.sf.ardengine.FontManager;
import net.sf.ardengine.events.EventType;
import net.sf.ardengine.events.IEvent;
import net.sf.ardengine.events.IListener;
import net.sf.ardengine.events.KeyEvent;
import net.sf.ardengine.input.InputManager;
import net.sf.ardengine.input.Keys;
import net.sf.ardengine.input.TextBox;

import java.util.ArrayList;
import java.util.List;

/**
 * ArdEngine console singleton,
 * renders heavily customized command TextBox at 1/3 of window
 *
 * To support new commands, check class ConsoleModule
 */
public class ConsoleUI extends TextBox {
    /**Singleton*/
    private static ConsoleUI INSTANCE;
    /**Default constant used, when determining console line count*/
    private static final float FONT_SPACING = 2.0f;

    /**True, if console is currently toggled on*/
    private boolean isShown = false;

    /**Console history*/
    private List<String> lines = new ArrayList<>();
    /**Command history*/
    private List<String> lastCommands = new ArrayList<>();
    /***Actual history listed command*/
    private int actualHistoryLine = 0;
    /**Maximum of lines, which can be renderered*/
    private int maximumLines;
    /**Size of used font*/
    private final int fontSize;
    /**Currently written command*/
    private String actualCommand = "";
    /**Console logic*/
    private Console commands;

    private ConsoleUI(){
        super(Core.renderer.getBaseWindowWidth(), Core.renderer.getBaseWindowHeight()/3);
        setBackgroundColor(Color.BLACK);
        setBackgroundOpacity(0.75f);
        setBorderOpacity(0);
        setTextColor(Color.SILVER);
        setStatic(true);

        fontSize = FontManager.getFont(FontManager.DEFAULT_KEY).getFontHeight();
        calculateLineLimit();
        lines.add("ArdEngine "+ Core.VERSION+" console");
        lines.add("Type \"console.help()\" for help...");
        rebuildText();

        InputManager.getInstance().registerListener(new IListener() {
            @Override
            public EventType getType() {
                return EventType.KEY_RELEASED;
            }

            @Override
            public void process(IEvent event) {
                if(InputManager.getInstance().getActiveKeyTyper() == INSTANCE){
                    if(lastCommands.size() == 0) return;

                    if(((KeyEvent)event).getAffectedKey() == Keys.KEY_UP){
                        actualHistoryLine--;
                        if(actualHistoryLine < 0) actualHistoryLine = lastCommands.size() -1;

                        actualCommand = lastCommands.get(actualHistoryLine);
                        rebuildText();
                    }else if(((KeyEvent)event).getAffectedKey() == Keys.KEY_DOWN){
                        actualHistoryLine++;
                        if(actualHistoryLine >= lastCommands.size()) actualHistoryLine =  0;

                        actualCommand = lastCommands.get(actualHistoryLine);
                        rebuildText();
                    }
                }
            }
        });

        commands = new Console();
        ConsoleUtils.addDefaultModules(commands);
    }

    /**
     * Splits given message to lines and and adds them to command history.
     * @param message Lines to render
     */
    private void addLines(String message){
        String[] lines = message.split("\n");
        for(String line : lines){
            this.lines.add(line);
        }
        rebuildText();
    }

    @Override
    public void keyTyped(char key) {
        actualCommand+=key;
        rebuildText();
    }

    @Override
    public void enterPressed() {
        String command = actualCommand;
        actualCommand = "";
        command(command);
    }

    @Override
    public void backspacePressed() {
        if(actualCommand.length() == 0) return;

        actualCommand = actualCommand.substring(0, actualCommand.length()-1);
        rebuildText();
    }

    private void command(String command){
        lines.add(command);
        rebuildText();
        try{
            lastCommands.add(command);
            actualHistoryLine = lastCommands.size();
            commands.process(command);
        }catch(Exception e){
            printError(e);
        }
    }

    /**
     * Creates String content for text from lines
     * and cuts old excessive ones
     */
    private void rebuildText(){
        StringBuilder text = new StringBuilder();

        while(lines.size() > maximumLines){
            lines.remove(0);
        }

        for(String line : lines){
            text.append(line);
            text.append('\n');
        }
        text.append(actualCommand);

        setText(text.toString());
    }

    /**
     * Updates limit of rendered command lines
     */
    private void calculateLineLimit(){
        maximumLines = (int)Math.floor(getHeight()/(fontSize+FONT_SPACING));
    }

    @Override
    public void setWidth(float width) {
        super.setWidth(width);
        calculateLineLimit();
    }

    /**
     * Shows/hides console UI
     */
    public static void toggle(){
        if(INSTANCE == null) throw  new Console.ConsoleException("Console is not initialized yet!");

        if(INSTANCE.isShown){
            Core.removeDrawable(INSTANCE, false);
            InputManager.getInstance().setActiveKeyTyper(null);
        }else{
            Core.addDrawable(INSTANCE);
            InputManager.getInstance().setActiveKeyTyper(INSTANCE);

            float windowWidth = Core.renderer.getBaseWindowWidth();
            float windowHeight = Core.renderer.getBaseWindowHeight()/3;

            if(INSTANCE.getHeight() != windowHeight || INSTANCE.getWidth() != windowWidth){
                INSTANCE.setHeight(windowHeight);
                INSTANCE.setWidth(windowWidth);
                INSTANCE.updateCollisionShape();
            }
        }
        INSTANCE.isShown = !INSTANCE.isShown;
    }

    /**
     * Writes given exception to console
     * @param e error
     */
    public static void printError(Throwable e){
        if(e == null || e.getMessage() == null) return;

        if(e instanceof Console.ConsoleException){
            print("CEE:"+e.getMessage());
        }else{
            StringBuilder error = new StringBuilder();
            error.append("ERROR:"+e.getMessage());
            error.append('\n');
            int errorPath = 0;
            for(StackTraceElement element : e.getStackTrace()){
                error.append(element.toString());
                error.append('\n');
                errorPath++;

                if(errorPath > 3){
                    break; //Console has too few free lines
                }
            }
            print(error.toString());
        }
    }

    /**
     * Writes given lines to console
     * @param message new line is created by \n
     */
    public static void print(String message){
        if(INSTANCE != null){
            INSTANCE.addLines(message);
        }
    }

    /**
     * Clears console content
     */
    public static void clear(){
        if(INSTANCE != null){
            INSTANCE.lines.clear();
            INSTANCE.rebuildText();
        }
    }

    /**
     * @return array of registered modules
     */
    public static String[] getRegisteredModules(){
        if(INSTANCE != null){
            return INSTANCE.commands.getModules();
        }
        return new String[0];
    }

    /**
     * @param moduleName Name of requested module
     * @return registered module with given name or throw exception
     */
    public static ConsoleModule getRegisteredModule(String moduleName){
        if(INSTANCE != null){
            return INSTANCE.commands.getModule(moduleName);
        }
        throw new Console.ConsoleException("Console is not initialized yet!");
    }

    /**
     * Adds new map of supported commands
     * @param customModule unique named, at "debug.enable", "debug" is name of module
     */
    public static void registerModule(ConsoleModule customModule){
        if(INSTANCE == null) throw new Console.ConsoleException("Console is not initialized yet!");

        INSTANCE.commands.registerModule(customModule);
    }

    /**
     * Tries to interpret and execute given String as command
     * @param command example: debug.enable(), sound.playMusic(path,1),
     */
    public static void processCommand(String command){
        if(INSTANCE != null){
            INSTANCE.command(command);
        }else{
            throw new Console.ConsoleException("Console is not initialized yet!");
        }
    }

    /**
     * Creates console UI instance based on selected renderer
     */
    public static void init(){
        INSTANCE = new ConsoleUI();
        InputManager.getInstance().registerListener(new IListener() {
            @Override
            public EventType getType() {
                return EventType.KEY_RELEASED;
            }

            @Override
            public void process(IEvent event) {
                if(((KeyEvent)event).getAffectedKey() == Keys.KEY_F3){
                    toggle();
                }
            }
        });
    }

    /**
     * Adds new map of supported commands
     * @param customModule unique named, at "debug.enable", "debug" is name of module
     */
    public static void attachModule(ConsoleModule customModule){
        INSTANCE.commands.registerModule(customModule);
    }

}