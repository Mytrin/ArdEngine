package net.sf.ardengine.rpg;

import com.google.gson.JsonObject;
import net.sf.ardengine.rpg.multiplayer.INetwork;
import net.sf.ardengine.rpg.multiplayer.INetworkMessage;

import java.util.LinkedList;
import java.util.List;

/**
 * Handles server game logic.
 */
public class AServerCore extends ANetworkCore{

    /**
     * @param network Responsible for communicating with other clients
     */
    protected AServerCore(INetwork network) {
        super(network);
    }

    @Override
    public void updateState(long delta) {
        if(isStarted){
            processReceivedMessages();
        }
    }

    private void processReceivedMessages(){
        /*List<INetworkMessage> messages = new LinkedList<>();

        for(INetworkMessage message : network.getMessages()){
            message.getMessage()
        }*/
    }


}
