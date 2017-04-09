package net.sf.ardengine.input.console;

import java.util.HashMap;
import java.util.Map;

/**
 * Processing core of console
 */
class Console {

    private Map<String, ConsoleModule> modules = new HashMap<>();

    /**
     * Tries to execute given commands, if registered
     * @param command complete input, example: "debug.enable()"
     */
    void process(String command){

        String[] commandSplit = command.split("\\.");

        if(commandSplit.length != 2) throw new ConsoleException("Incomplete command!");

        int argumentStart = commandSplit[1].indexOf('(');

        if( argumentStart == -1) throw new ConsoleException("Incomplete command!");

        String[] arguments = commandSplit[1].substring(argumentStart+1, commandSplit[1].indexOf(')')).split(", ");
        String commandName = commandSplit[1].substring(0, commandSplit[1].indexOf('('));

        if(modules.containsKey(commandSplit[0])){
            modules.get(commandSplit[0]).process(commandName, arguments);
        }else{
            throw new ConsoleException("Command module with name "+commandSplit[0]+" does not exists!");
        }
    }

    /**
     * Adds new map of supported commands
     * @param module unique named, at "debug.enable", "debug" is name of module
     */
    void registerModule(ConsoleModule module){
        String moduleKey = module.getName();

        if(!modules.containsKey(moduleKey)){
            modules.put(moduleKey, module);
        }else{
            throw new ConsoleException("Module with name"+moduleKey+" is already registered!");
        }
    }

    /**
     * @return array of registered modules
     */
    public String[] getModules(){
        return modules.keySet().toArray(new String[modules.size()]);
    }

    /**
     * @param module name of requested module
     * @return registered module with given name or throw exception
     */
    public ConsoleModule getModule(String module){
        if(modules.containsKey(module)){
            return modules.get(module);
        }else{
            throw  new ConsoleException("Module is not registered!");
        }
    }

    /***
     * Custom console exception
     */
    static class ConsoleException extends RuntimeException{
        /**
         * @param error reason, while this exception was thrown
         */
        ConsoleException(String error){
            super(error);
        }
    }

}