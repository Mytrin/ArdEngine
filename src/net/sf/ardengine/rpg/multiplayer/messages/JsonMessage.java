package net.sf.ardengine.rpg.multiplayer.messages;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import net.sf.ardengine.rpg.multiplayer.network.INetworkMessage;
import net.sf.ardengine.rpg.multiplayer.INetworkedNode;

/**
 * Represents universal message, which is sent between NetworkCores
 */
public class JsonMessage {

    /**Type property of JSON message*/
    public static final String TYPE_ATTR = "type";
    /**Type of Unknown JSON message*/
    public static final String UNKNOWN_TYPE = "not-json";
    /**Unique identification of Node in Game and Worlds*/
    public static final String TARGET_NODE_ATTR = "node-id";
    /**Inner JSON content of message*/
    public static final String CONTENT_ATTR = "content";
    /**Time, when message was sent*/
    public static final String TIMESTAMP = "timestamp";

    /**Sent JSON*/
    public final JsonObject json;
    /**Can be null in case if JsonMessage is for sending*/
    public final INetworkMessage sourceMessage;
    /**
     * @param type purpose of this message
     * @param content Sent JSON object
     * @param targetNode node, about which is state informs
     */
    public JsonMessage(String type, JsonObject content, INetworkedNode targetNode) {
        this.json = new JsonObject();
        this.json.add(CONTENT_ATTR, content);
        this.json.addProperty(TYPE_ATTR, type);
        this.json.addProperty(TARGET_NODE_ATTR, (targetNode!=null?targetNode.getID():null));
        this.sourceMessage = null;
    }

    /**
     * Reconstructs JSON message
     * @param parser Used JSON parser
     * @param message received message
     */
    public JsonMessage(JsonParser parser, INetworkMessage message) {
        this.json = parseString(parser, message);
        this.sourceMessage = message;
    }

    private JsonObject parseString(JsonParser parser, INetworkMessage message){
        JsonObject jsonObject;
        try{
            jsonObject = new JsonParser().parse(message.getMessage()).getAsJsonObject();
        }catch (Exception e){
            jsonObject = new JsonObject();
            jsonObject.addProperty(CONTENT_ATTR, message.getMessage());
            jsonObject.addProperty(TYPE_ATTR, UNKNOWN_TYPE);
        }
        return jsonObject;
    }

    /**
     * @return purpose of this message
     */
    public String getType() {
        return getValueAsString(TYPE_ATTR);
    }

    /**
     * @return id of target node or null, if none
     */
    public String getTargetNodeID() {
        return getValueAsString(TARGET_NODE_ATTR);
    }

    /**
     * @return Inner JSON of this message
     */
    public JsonObject getContent() {
        JsonElement content = json.get(CONTENT_ATTR);

        if(content != null){
            return content.getAsJsonObject();
        }

        return null;
    }

    /**
     * @param key property of json
     * @return specified property value or null, if none
     */
    public String getValueAsString(String key){
        JsonPrimitive property = json.getAsJsonPrimitive(key);
        if(property != null){
            return property.getAsString();
        }
        return null;
    }

    /**
     * @param key property of json
     * @return specified property value or null, if none or non integer
     */
    public Integer getValueAsInt(String key){
        JsonPrimitive property = json.getAsJsonPrimitive(key);
        if(property != null){
            try{
                return property.getAsInt();
            }catch(Exception e){}
        }
        return null;
    }

    /**
     * @param key property of json
     * @return specified property value or null, if none or non long
     */
    public Long getValueAsLong(String key){
        JsonPrimitive property = json.getAsJsonPrimitive(key);
        if(property != null){
            try{
                return property.getAsLong();
            }catch(Exception e){}
        }
        return null;
    }

    /**
     * @param key property of json
     * @return specified property value or null, if none or non boolean
     */
    public Boolean getValueAsBoolean(String key){
        JsonPrimitive property = json.getAsJsonPrimitive(key);
        if(property != null){
            try{
                return property.getAsBoolean();
            }catch(Exception e){}
        }
        return null;
    }

    @Override
    public String toString() {
        return json.toString();
    }
}
