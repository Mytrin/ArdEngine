package net.sf.ardengine.rpg.multiplayer;

import com.google.gson.JsonObject;
import net.sf.ardengine.rpg.multiplayer.messages.DeltaStateMessage;
import net.sf.ardengine.rpg.multiplayer.messages.JsonMessage;

/**
 * Interface for Nodes synchronized by ANetworkCore.
 */
public interface INetworkedNode {

    class StoredStates{
        /**Capacity of state buffer*/
        public static final int STATES_BUFFER_SIZE = 10;
        private static final int CLEAR_OFFSET = 5;

        public static final int FRAME_INDEFINITE = -1;

        //states may not arrive in order
        private JsonObject[] storedStates = new JsonObject[STATES_BUFFER_SIZE];

        //How many times we had to use lastState
        private int fallBackCount = 0;
        private JsonObject lastState;

        private final INetworkedNode targetNode;

        /**
         * @param targetNode Node, which states are stored
         */
        StoredStates(INetworkedNode targetNode) {
            this.targetNode = targetNode;
        }

        private void receive(JsonMessage message){
            int stateIndex = message.json.get(DeltaStateMessage.STATE_INDEX).getAsInt();
            storedStates[stateIndex] = message.json.getAsJsonObject(JsonMessage.CONTENT);
        }

        private void update(int currentStateIndex, int currentFrame){
            clearOldStates(currentStateIndex);

            int actualFrame = currentFrame;
            JsonObject actualState = storedStates[currentStateIndex];

            if(actualState!=null){
                //Saving for situations, where is actualState missing
                lastState = actualState;
                fallBackCount = 0;
            }else{
                fallBackCount++;
                actualState = lastState;
                actualFrame += ANetworkCore.FRAMES_PER_STATE*fallBackCount;
            }

            updateClientState(actualState, currentStateIndex, actualFrame);
        }

        private void updateClientState(JsonObject actualState, int actualStateIndex, int actualFrame){
            if(actualState != null){
                for(int i=1; i <= 3 ; i++){
                    JsonObject nextState = storedStates[bufferIndex(actualStateIndex+i)];
                    if(nextState != null){
                        targetNode.updateClientState(actualState, nextState, actualFrame, ANetworkCore.FRAMES_PER_STATE*i);
                        return;
                    }
                }

                //No info, keep doing whatever you were doing before
                targetNode.updateClientState(actualState, null, actualFrame, FRAME_INDEFINITE);
            } //else there is nothing we can do
        }

        private void clearOldStates(int actualIndex){
            int clearIndex = (actualIndex + CLEAR_OFFSET)%STATES_BUFFER_SIZE;
            storedStates[clearIndex] = null;
        }

        private int bufferIndex(int stateIndex){
            return stateIndex%STATES_BUFFER_SIZE;
        }

        private JsonMessage createJsonMessage(int currentStateIndex, long serverTimestamp){
            return new DeltaStateMessage(targetNode, serverTimestamp, currentStateIndex);
        }

    }

    /**
     * @return Unique identification of this Node in Game and Worlds
     */
    String getID();

    /**
     * Handles received state updates from Server (Override for each Node)
     * Method for user, where he can define interpolation between states
     * @param lastState last known state
     * @param nextState next known state or null, if unknown
     * @param actFrame  which frame should method interpolate for.
     * @param endFrame Frame, when should this node be synchronized with nextState.
     *                 If nextState is null, endFrame value is FRAME_INDEFINITE.
     */
    void updateClientState(JsonObject lastState, JsonObject nextState, int actFrame, int endFrame);

    /**Handles standard game logic, which would be at Node.updateLogic() for simple games*/
    void updateServerState();

    /**
     * @return Changed properties of this object prepared for sending to ClientNetworkCores
     */
    JsonObject getJSONDeltaState();

    /**
     * @return Complete info about this object prepared for sending to ClientNetworkCores
     */
    JsonObject getJSONState();

    /**
     * For user, indicator then Server should not send State of this Node, because nothing changed.
     * Please use carefully for moving objects,
     * as some clients may not receive info about stopped movement.
     * @return true, if changes made by updateServerState() should be sent to clients by JSON
     */
    default boolean hasChangedState(){
        return true;
    }

    /**
     * @return Object containing stored JSON states, which were received from server
     */
    StoredStates getStoredStates();

    /**
     * DO NOT OVERRIDE, USED BY NETWORK CORE
     * to trigger state updates received from server
     * @param actualIndex index of current state
     * @param passedFrames Frames passed since current state
     */
    default void triggerState(int actualIndex, int passedFrames){
        getStoredStates().update(actualIndex, passedFrames);
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
     * to prepare update about state from clients
     * @param currentIndex current Server index
     * @param serverTimestamp server time, at which si message sent
     */
    default JsonMessage getJsonMessage(int currentIndex, long serverTimestamp){
       return getStoredStates().createJsonMessage(currentIndex, serverTimestamp);
    }

}
