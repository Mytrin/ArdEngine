package net.sf.ardengine.io.console;

/**
 * Basic console command
 */
public interface ConsoleCommand {

    /**
     * sound.play(path/to/ogg, 1) results at calling  process(new String[]{"path/to/ogg", "1"})
     * @param arguments parsed command params
     */
    public void process(String[] arguments);

}