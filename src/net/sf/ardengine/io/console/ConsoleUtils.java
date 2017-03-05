package net.sf.ardengine.io.console;

import net.sf.ardengine.Core;
import net.sf.ardengine.io.Config;
import net.sf.ardengine.io.SevereFormatter;

import java.io.File;

/**
 * Implementation of default console commands
 */
public class ConsoleUtils {

    private static final ConsoleModule debugModule = new ConsoleModule("debug");
    private static final ConsoleModule statsModule = new ConsoleModule("stats");
    private static final ConsoleModule configModule = new ConsoleModule("config");
    private static final ConsoleModule consoleModule = new ConsoleModule("console");

    private static final ConsoleModule mwhModule = new ConsoleModule("mwh");

    static{
        //DEBUG
        debugModule.addCommand("enable", (arguments) -> {
                Core.debugMode = true;
        });
        debugModule.addCommand("disable", (arguments) -> {
                Core.debugMode = false;
        });
        debugModule.addCommand("renderer", (arguments) -> {
            ConsoleUI.print("Used renderer implementation: "+ Core.renderer.getClass().getName());
        });
        debugModule.addCommand("mwh", (arguments) -> {
            ConsoleUI.print("Been poking around source code, have you? :-)");
            ConsoleUI.registerModule(mwhModule);
        });
        //STATS
        statsModule.addCommand("drawables", (arguments) -> {
                ConsoleUI.print("Current game drawables: "+ Core.getDrawableCount());
        });
        statsModule.addCommand("nodes", (arguments) -> {
                ConsoleUI.print("Current game nodes: "+ Core.getNodeCount());
        });
        statsModule.addCommand("overview", (arguments) -> {
                ConsoleUI.print("Current game drawables: "+ Core.getDrawableCount()+" of which "+ Core.getNodeCount()+" are nodes.");
        });
        statsModule.addCommand("version", (arguments) -> {
            ConsoleUI.print("Current ArdEngine version: "+ Core.VERSION+(Core.DEBUG?" DEBUG release":""));
        });
        //CONFIG
        configModule.addCommand("load", (arguments) -> {
            if(arguments[0] != null  && !arguments[0].equals("")){
                File config = new File(arguments[0]);
                if(config.exists()){
                    Config.loadConfig(config);
                }else{
                    ConsoleUI.print("Configuration file not found!");
                }
            }else{
                ConsoleUI.print("Missing path to configuration file!");
            }});
        configModule.addCommand("set", (arguments) -> {
            if(arguments[0] != null  && !arguments[0].equals("")){
                if(arguments[1] != null  && !arguments[1].equals("")){
                    Config.set(arguments[0], arguments[1]);
                }else{
                    ConsoleUI.print("Missing config tag value!");
                }
            }else{
                ConsoleUI.print("Missing config tag name!");
            }});
        configModule.addCommand("get", (arguments) -> {
            if(arguments[0] != null  && !arguments[0].equals("")){
                ConsoleUI.print(arguments[0]+": "+Config.getString(arguments[0], "undefined"));
            }else{
                ConsoleUI.print("Missing config tag name!");
            }});
        //CONSOLE
        consoleModule.addCommand("hide", (arguments) -> {
            ConsoleUI.toggle();
        });
        consoleModule.addCommand("clear", (arguments) -> {
            ConsoleUI.clear();
        });
        consoleModule.addCommand("help", (arguments) -> {

            String[] items;
            StringBuilder printedItems = new StringBuilder();

            if(arguments[0] == null  || arguments[0].equals("")){
                ConsoleUI.print("Type console.help(moduleName) to list available module commands.");
                items = ConsoleUI.getRegisteredModules();
            }else{
                ConsoleUI.print("Available commands of module "+arguments[0]+":");
                items = ConsoleUI.getRegisteredModule(arguments[0]).getCommands();
            }

            for(int i=0; i < items.length-1; i++){
                printedItems.append(items[i]);
                printedItems.append(", ");
            }
            printedItems.append(items[items.length-1]);

            ConsoleUI.print(printedItems.toString());
        });
        //MYTRIN WAS HERE
        mwhModule.addCommand("errorQuote", (arguments) -> {
            ConsoleUI.print(SevereFormatter.loadRandomHint());
        });
    }


    /**
     * Adds all default modules to given console instance
     * @param console blank Console implementation
     */
    public static void addDefaultModules(Console console){
        console.registerModule(debugModule);
        console.registerModule(statsModule);
        console.registerModule(configModule);
        console.registerModule(consoleModule);
    }

}