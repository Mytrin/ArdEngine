package net.sf.ardengine.input.console;

import java.util.HashMap;
import java.util.Map;

/**
 * Class, which processes commands from ConsoleUI
 *
 * debug.enable() means, that process("enable", null)
 * will be called on ConsoleModule with name "debug"
 *
 * Attach new modules by ConsoleUI.attachModule()
 */
public class ConsoleModule {

    /**Registered module commands*/
    private final Map<String, ConsoleCommand> commands;

    /**
     * Unique name of module, at debug.enable() debug is the name
     */
    private final String name;

    /**
     * @param name Unique name of module, at debug.enable() debug is the name
     */
    public ConsoleModule(String name){
        this.name = name;
        this.commands = new HashMap<>();
    }

    /**
     * Example: sound.play(path/to/ogg, 1)
     * @param commandName "play" at this example
     * @param arguments parsed command params, new String[]{"path/to/ogg", "1"} at this example
     */
    public void process(String commandName, String[] arguments){
        ConsoleCommand consCommand = commands.get(commandName);

        if(consCommand != null){
            consCommand.process(arguments);
        }else{
            throw new Console.ConsoleException("Command "+commandName+" does not exist in module "+name+"!");
        }
    }

    /**
     * Inserts given command to module commands, Example: debug.enable()
     * @param commandName in this example "enable"
     * @param command command implementation
     */
    public void addCommand(String commandName, ConsoleCommand command){
        if(commands.containsKey(commandName)) throw new Console.ConsoleException("Command "+command+" already exists in module "+name+"!");

        commands.put(commandName, command);
    }

    /**
     * @return Unique name of module, at debug.enable() debug is the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return array of registered commands
     */
    public String[] getCommands(){
        return commands.keySet().toArray(new String[commands.size()]);
    }
}