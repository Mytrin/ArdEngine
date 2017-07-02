package net.sf.ardengine.rpg.multiplayer.udplib;

import com.gmail.lepeska.martin.udplib.AGroupNetwork;
import com.gmail.lepeska.martin.udplib.StoredMessage;
import com.gmail.lepeska.martin.udplib.client.ClientGroupNetwork;
import com.gmail.lepeska.martin.udplib.client.GroupUser;
import com.gmail.lepeska.martin.udplib.server.ServerGroupNetwork;
import com.gmail.lepeska.martin.udplib.util.ConfigLoader;
import net.sf.ardengine.rpg.multiplayer.network.*;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.List;

/**
 * Layer between ArdEngine Network and UDPLib Network implementation.
 */
public class UDPLibNetworkAdapter implements INetwork {

    private final AGroupNetwork networkImpl;
    private final LinkedList<UDPLibNetworkListener> mappedListeners = new LinkedList<>();
    private final String serverAddress;

    private UDPLibNetworkAdapter(String userName, String groupPassword, String hostAddress, String groupAddress, int port) throws UnknownHostException {
        this.networkImpl = new ServerGroupNetwork(userName, groupPassword, hostAddress, groupAddress, port,
                ConfigLoader.getInt("user-info-period", 5000), ConfigLoader.getInt("dead-time", 2000));
        this.serverAddress = hostAddress;
    }

    /**
     * Prepares new GroupServerThread bound on interface of given hostAddress with given password and default group address loaded from configuration file.
     *
     * @param userName User's name in network
     * @param groupPassword Password required to access this group or null, if none
     * @param hostAddress Address in network interface, which should server socket use
     * @param groupAddress Address of used multi cast group
     * @param port Port of server socket
     * @return new UDPLibNetworkAdapter Server instance with given setup
     * @throws UnknownHostException thrown if UDPLib fails to locate NetWorkInterface of given IP
     */
    public static UDPLibNetworkAdapter createNetwork(String userName, String groupPassword, String hostAddress, String groupAddress, int port) throws UnknownHostException {
        return new UDPLibNetworkAdapter(userName, groupPassword, hostAddress, groupAddress, port);
    }

    /**
     * Prepares new GroupServerThread on interface of given hostAddress with given password and default group address loaded from configuration file.
     *
     * @param userName User's name in network
     * @param groupPassword Password required to access this group or null, if none
     * @param hostAddress Address in network interface, which should server socket use
     * @param port Port of server socket
     * @return new UDPLibNetworkAdapter Server instance with given setup
     * @throws UnknownHostException thrown if UDPLib fails to locate NetWorkInterface of given IP
     */
    public static UDPLibNetworkAdapter createNetwork(String userName, String groupPassword, String hostAddress, int port) throws UnknownHostException{
        return new UDPLibNetworkAdapter(userName, groupPassword, hostAddress, ConfigLoader.getString("default-group", "225.226.227.228"), port);
    }

    /**
     * Prepares new GroupServerThread with given password and default values loaded from configuration file.
     *
     * @param userName User's name in network
     * @param groupPassword Password required to access this group or null, if none
     * @return new UDPLibNetworkAdapter Server instance with given setup
     * @throws UnknownHostException thrown if UDPLib fails to locate NetWorkInterface of given IP
     */
    public static UDPLibNetworkAdapter createNetwork(String userName, String groupPassword) throws UnknownHostException{
        return new UDPLibNetworkAdapter(userName, groupPassword, ConfigLoader.getString("default-server-ip", "0.0.0.0"),
                ConfigLoader.getString("default-group", "225.226.227.228"), ConfigLoader.getInt("default-port", 52511));
    }

    private UDPLibNetworkAdapter(String userName, String groupPassword, String serverAddress, int port) throws UnknownHostException{
        this.networkImpl = new ClientGroupNetwork(userName, groupPassword, serverAddress, port);
        this.serverAddress = serverAddress;
    }

    /**
     * Prepares new GroupClientThread bound on interface of given hostAddress with given password and server address.
     *
     * @param userName User's name in network
     * @param groupPassword Password required to access this group or null, if none
     * @param serverAddress Address of group owner
     * @param port Port of server socket
     * @return new UDPLibNetworkAdapter Client instance with given setup
     * @throws UnknownHostException thrown if UDPLib fails to connect to the server
     */
    public static UDPLibNetworkAdapter joinNetwork(String userName, String groupPassword, String serverAddress, int port) throws UnknownHostException{
        return new UDPLibNetworkAdapter(userName, groupPassword, serverAddress, port);
    }

    /**
     *  Prepares new GroupClientThread with given password and default values loaded from configuration file.
     *
     * @param userName User's name in network
     * @param groupPassword Password required to access this group or null, if none
     * @param serverAddress Address of group owner
     * @return new UDPLibNetworkAdapter Client instance with given setup
     * @throws UnknownHostException thrown if UDPLib fails to connect to the server
     */
    public static UDPLibNetworkAdapter joinNetwork(String userName, String groupPassword, String serverAddress) throws UnknownHostException{
        return new UDPLibNetworkAdapter(userName, groupPassword, serverAddress, ConfigLoader.getInt("default-port", 52511));
    }

    @Override
    public void start() {
        networkImpl.start();
    }

    @Override
    public void leave() {
        networkImpl.leave();
    }

    @Override
    public List<INetworkPlayer> getCurrentUsers() {
        LinkedList<INetworkPlayer> currentPlayer = new LinkedList<>();
        networkImpl.getCurrentUsers().stream().forEach((GroupUser groupUser) ->
                currentPlayer.add(new UDPLibNetworkPlayer(groupUser))
        );
        return currentPlayer;
    }

    @Override
    public INetworkPlayer getServerUser() {
        UDPLibNetworkPlayer user = null;
        try{
            InetAddress serverIP = InetAddress.getByName(serverAddress);
            for( GroupUser groupUser : networkImpl.getCurrentUsers()) {
                if(groupUser.ip.equals(serverIP)){
                    user = new UDPLibNetworkPlayer(groupUser);
                }
            }
        }catch(Exception e){}

        return user;
    }

    @Override
    public List<INetworkMessage> getMessages() {
        LinkedList<INetworkMessage> receivedMessages = new LinkedList<>();
        networkImpl.getMessages().stream().forEach((StoredMessage msg) ->
                receivedMessages.add(new UDPLibNetworkMessage(msg))
        );
        return receivedMessages;
    }

    @Override
    public String getUserName() {
        return networkImpl.getUserName();
    }

    @Override
    public void sendMessage(INetworkPlayer target, String message) {
        for(GroupUser user : networkImpl.getCurrentUsers()){
            if(user.name.equals(target.getName()) && user.ip.equals(target.getIP())){
                networkImpl.sendMessage(user, message);
                break;
            }
        }
    }

    @Override
    public void sendBroadcastMessage(String message) {
        networkImpl.sendGroupMessage(message);
    }

    @Override
    public void addListener(INetworkListener listener) {
        UDPLibNetworkListener mappedListener = new UDPLibNetworkListener(listener);
        mappedListeners.add(mappedListener);
        networkImpl.addListener(mappedListener);
    }

    @Override
    public void removeListener(INetworkListener listener) {
        for(UDPLibNetworkListener netListener : mappedListeners){
            if(netListener.gameListener.equals(listener)){
                networkImpl.removeListener(netListener);
                break;
            }
        }
    }

    @Override
    public void shareFile(File file, String fileName, INetworkFileListener listener) {
        networkImpl.shareFile(file, fileName, new UDPLibFileListener(listener));
    }
}
