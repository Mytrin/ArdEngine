package net.sf.ardengine.rpg.multiplayer;

import com.google.gson.JsonParser;
import net.sf.ardengine.Core;
import net.sf.ardengine.input.console.ConsoleUI;
import net.sf.ardengine.rpg.multiplayer.messages.JsonMessage;
import net.sf.ardengine.rpg.multiplayer.messages.JsonMessageHandler;
import net.sf.ardengine.rpg.multiplayer.network.INetwork;
import net.sf.ardengine.rpg.multiplayer.network.INetworkListener;
import net.sf.ardengine.rpg.multiplayer.network.INetworkMessage;
import net.sf.ardengine.rpg.multiplayer.network.INetworkPlayer;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Handles game logic.
 */
public abstract class ANetworkCore {
    /**State update frequency (How many game frames it takes to advance to next state)*/
    public static final int FRAMES_PER_STATE = 3;

    /**Index of state in NetworkedNode's buffer*/
    protected int actualIndex = 0;
    /**progress to new state*/
    protected int actualFrame = 0;
    /**true, if actualIndex has been changed this update*/
    protected boolean changedIndex = false;

    /**True, if Core is currently processing*/
    protected boolean isStarted = true;

    /**Nodes, about which NetworkCore shares information with other NetworkCores*/
    protected final HashMap<String, INetworkedNode> synchronizedNodes = new HashMap<>();

    /**Responsible for communicating with other clients*/
    protected final INetwork network;

    private final LinkedList<DelayedTask> tasks = new LinkedList<>();

    protected final HashMap<String, JsonMessageHandler> handlers = new HashMap<>();
    protected JsonParser parser = new JsonParser();

    protected final HashMap<String, File> receivedFiles = new HashMap<>();
    private final INetworkListener coreListener = new INetworkListener() {
        @Override
        public void joined(INetworkPlayer me) {
            ConsoleUI.print("Joined network as "+me.getIP());
        }

        @Override
        public void userKicked(INetworkPlayer who) {
            ConsoleUI.print("User disconnected "+who.getName());
        }

        @Override
        public void userJoined(INetworkPlayer who) {
            ConsoleUI.print("User joined "+who.getName());
        }

        @Override
        public void mesageReceived() {}

        @Override
        public void kicked() {
            ConsoleUI.print("Kicked from network!");
        }

        @Override
        public void fileReceived(String fileID, File file) {
            ConsoleUI.print("Received file "+fileID);
            receivedFiles.put(fileID, file);
        }
    };

    /**
     * @param network Responsible for communicating with other clients
     */
    protected ANetworkCore(INetwork network) {
        this.network = network;
        this.network.addListener(coreListener);
    }

    /**
     * Processes received messages and starts updateCoreLogic()
     * @param delta Time since last update
     */
    public final void updateState(long delta){
        int passedFrames = countPassedFrames(delta);

        if(isStarted){
            updateStateIndex(passedFrames);
            processReceivedMessages();
            executeTasks(delta);
            updateCoreLogic(delta, passedFrames);
        }
    }

    /**
     * Updates Server/Client logic
     * @param delta time passed since last update
     * @param passedFrames Frames passed since last update
     */
    protected abstract void updateCoreLogic(long delta, int passedFrames);

    private void processReceivedMessages(){
        network.getMessages().forEach((INetworkMessage iNetworkMessage) -> {
            JsonMessage receivedMessage = new JsonMessage(parser, iNetworkMessage);
            handle(receivedMessage);
        });
    }

    private void handle(JsonMessage message){
        JsonMessageHandler handler = handlers.get(message.getType());
        if(handler != null){
            handler.handle(message);
        }else{
            Logger.getLogger(ANetworkCore.class.getName()).warning(
                    "Unspecified type of JSONMessage: "+message.getType()+". Message ignored.");
        }
    }

    private void updateStateIndex(int passedFrames){
        actualFrame+=passedFrames;

        if(actualFrame<FRAMES_PER_STATE-1){
            changedIndex = false;
            return;
        }

        while(actualFrame>FRAMES_PER_STATE-1){
            actualFrame-=FRAMES_PER_STATE;
            actualIndex++;
            changedIndex = true;

            if(actualIndex==INetworkedNode.StoredStates.STATES_BUFFER_SIZE){
                actualIndex=0;
            }
        }
    }

    /**
     * @return Frames passed since last renderer update
     */
    private int countPassedFrames(long delta){
        float frameLength = 1000f/(float)Core.renderer.getDesiredFPS();
        return (int)(delta/frameLength);
    }

    /**
     * Inserts node into list of nodes, which state is shared with server/clients.
     * @param node added Node with unique ID
     */
    public void addNode(INetworkedNode node){
        synchronizedNodes.put(node.getID(), node);
    }

    /**
     * Removes node from list of nodes, which state is shared with server/clients.
     * @param node removed Node with unique ID
     */
    public void removeNode(INetworkedNode node){
        synchronizedNodes.remove(node.getID());
    }

    /**
     * Starts Core INetwork update/notify process
     */
    public void start(){
        isStarted = true;
    }

    private void executeTasks(long delta){
        List<DelayedTask> finishedTasks = new LinkedList<>();
        for(DelayedTask task : tasks){
            if(task.timePassed(delta)){
                task.execute();
                finishedTasks.add(task);
            }
        }
        tasks.removeAll(finishedTasks);
    }

    /**
     * @param when ms to wait before action.execute()
     * @param action action to execute in future
     */
    public void addDelayedTask(long when, IDelayedAction action){
        tasks.add(new DelayedTask(when) {
            @Override
            protected void execute() {
                action.execute();
            }
        });
    }
}
