package net.sf.ardengine.rpg.multiplayer;

import com.google.gson.JsonObject;

/**
 * Represents universal message, which is sent betweend NetwrkCores
 */
public class JsonMessage {

    /**Type of JSON message*/
    public static final String TYPE = "type";
    /**Unique identification of Node in Game and Worlds*/
    public static final String TARGET_NODE = "node-id";
    /**Type of JSON message*/
    public static final String CONTENT = "content";

    /**Sent JSON*/
    public final JsonObject json;

    /**
     * @param type purpose of this message
     * @param content Sent JSON object
     */
    public JsonMessage(String type, JsonObject content, INetworkedNode targetNode) {
        this.json = new JsonObject();
        this.json.add(CONTENT, content);
        this.json.addProperty(TYPE, type);
        this.json.addProperty(TARGET_NODE, targetNode.getID());
    }

    public JsonMessage(JsonObject receivedJSON) {
        this.json = receivedJSON;
    }

    /**
     * @return purpose of this message
     */
    public String getType() {
        return json.getAsJsonPrimitive(TYPE).getAsString();
    }
}
