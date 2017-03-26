package net.sf.ardengine.rpg.multiplayer;

import com.google.gson.JsonObject;

/**
 * Interface for Nodes synchronized by ANetworkCore.
 */
public interface INetworkedNode {

    class StoredStates{
        /**Capacity of state buffer*/
        public static final int STATES_BUFFER_SIZE = 100;
        private static final int CLEAR_OFFSET = 50;

        private static final String STATE_INDEX = "state-index";
        private static final String TYPE = "node-state";

        //states may not arrive in order
        private JsonObject[] storedStates = new JsonObject[STATES_BUFFER_SIZE];

        private final INetworkedNode targetNode;

        /**
         * @param targetNode Node, which states are stored
         */
        public StoredStates(INetworkedNode targetNode) {
            this.targetNode = targetNode;
        }

        private void receive(JsonMessage message){
            int stateIndex = message.json.get(STATE_INDEX).getAsInt();
            storedStates[stateIndex] = message.json.getAsJsonObject(JsonMessage.CONTENT);
        }

        private void update(int currentStateIndex){
            clearOldStates(currentStateIndex);

            if(storedStates[currentStateIndex] != null){
                targetNode.updateClientState(storedStates[currentStateIndex]);
            }
        }

        private void clearOldStates(int actualIndex){
            int clearIndex = (actualIndex + CLEAR_OFFSET)%100;
            storedStates[clearIndex] = null;
        }

        /**
         * @param state JSON state of current Node
         * @return JSON message prepared for sending to clients,
         * containing additional necessary information
         */
        private JsonMessage createJsonMessage(int currentStateIndex, JsonObject state){
            JsonMessage message = new JsonMessage(TYPE, state, targetNode);
            message.json.addProperty(STATE_INDEX, currentStateIndex);

            return message;
        }

    }

    /**
     * @return Unique identification of this Node in Game and Worlds
     */
    public String getID();

    /**Handles received state updates from Server*/
    public void updateClientState(JsonObject state);

    /**Handles standard game logic, which would be at Node.updateLogic() for simple games*/
    public void updateServerState();

    /**
     * @return Changed properties of this object prepared for sending to ClientNetworkCores
     */
    public JsonObject getJSONState();

    /**
     * For server
     * @return true, if changes made by updateServerState() should be sent to clients by JSON
     */
    public boolean hasChangedState();

    /**
     * @return Object containing stored JSON states, which were received from server
     */
    public StoredStates getStoredStates();

    /**
     * DO NOT OVERRIDE, USED BY NETWORK CORE
     * to trigger state updates received from server
     * @param passedFrames Frames passed since last update
     */
    default void triggerState(int passedFrames){
        getStoredStates().update(passedFrames);
    }

    /**
     * DO NOT OVERRIDE, USED BY NETWORK CORE
     * to prepare State received from server
     * @param receivedState info about Node from future
     */
    default void receiveState(JsonMessage receivedState){
        getStoredStates().receive(receivedState);
    }

    /**
     * DO NOT OVERRIDE, USED BY NETWORK CORE
     * to prepare update about state fro clients
     * @param currentIndex current Server index
     */
    default JsonMessage getJsonMessage(int currentIndex){
       return getStoredStates().createJsonMessage(currentIndex, getJSONState());
    }

}
