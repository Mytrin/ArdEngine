package net.sf.ardengine.rpg.multiplayer;

/**
 * Handler for specific type of incoming JSON Messages,
 * registered in ANetworkCore HashMap.
 */
@FunctionalInterface
public interface JsonMessageHandler {

    /**
     * Handles incoming message with type, for which is thi handler
     * registered in ANetworkCore HashMap
     * @param message
     */
    public void handle(JsonMessage message);

}
