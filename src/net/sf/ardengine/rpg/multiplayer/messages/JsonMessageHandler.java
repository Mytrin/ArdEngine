package net.sf.ardengine.rpg.multiplayer.messages;

/**
 * Handler for specific type of incoming JSON Messages,
 * registered in ANetworkCore HashMap.
 */
@FunctionalInterface
public interface JsonMessageHandler {

    /**
     * Handles incoming message with type, for which iss thi handler
     * registered in ANetworkCore HashMap
     * @param message incoming message
     */
    public void handle(JsonMessage message);

}
