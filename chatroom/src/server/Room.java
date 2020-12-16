package server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Random;

public class Room implements AutoCloseable {
    private static SocketServer server;// used to refer to accessible server functions
    private String name;
    private final static Logger log = Logger.getLogger(Room.class.getName());

    // Commands
    private final static String COMMAND_TRIGGER = "/";
    private final static String PRIVATE_MSG_TRIGGER = "@";
    private final static String CREATE_ROOM = "createroom";
    private final static String JOIN_ROOM = "joinroom";
    private final static String ROLL = "roll";
    private final static String FLIP = "flip";
    private final static String MUTE = "mute";
    private final static String UNMUTE = "unmute";
    
    Random rand = new Random();

    public Room(String name) {
	this.name = name;
    }

    public static void setServer(SocketServer server) {
	Room.server = server;
    }

    public String getName() {
	return name;
    }

    private List<ServerThread> clients = new ArrayList<ServerThread>();

    protected synchronized void addClient(ServerThread client) {
	client.setCurrentRoom(this);
	if (clients.indexOf(client) > -1) {
	    log.log(Level.INFO, "Attempting to add a client that already exists");
	}
	else {
	    clients.add(client);
	    if (client.getClientName() != null) {
		client.sendClearList();
		sendConnectionStatus(client, true, "joined the room " + getName());
		updateClientList(client);
	    }
	}
    }

    private void updateClientList(ServerThread client) {
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) {
	    ServerThread c = iter.next();
	    if (c != client) {
		boolean messageSent = client.sendConnectionStatus(c.getClientName(), true, null);
	    }
	}
    }

    protected synchronized void removeClient(ServerThread client) {
	clients.remove(client);
	if (clients.size() > 0) {
	    // sendMessage(client, "left the room");
	    sendConnectionStatus(client, false, "left the room " + getName());
	}
	else {
	    cleanupEmptyRoom();
	}
    }

    private void cleanupEmptyRoom() {
	// If name is null it's already been closed. And don't close the Lobby
	if (name == null || name.equalsIgnoreCase(SocketServer.LOBBY)) {
	    return;
	}
	try {
	    log.log(Level.INFO, "Closing empty room: " + name);
	    close();
	}
	catch (Exception e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    protected void joinRoom(String room, ServerThread client) {
	server.joinRoom(room, client);
    }

    protected void joinLobby(ServerThread client) {
	server.joinLobby(client);
    }

    /***
     * Helper function to process messages to trigger different functionality.
     * 
     * @param message The original message being sent
     * @param client  The sender of the message (since they'll be the ones
     *                triggering the actions)
     */
    private boolean processCommands(String message, ServerThread client) {
	boolean wasCommand = false;
	try {
	    if (message.indexOf(COMMAND_TRIGGER) > -1) {
		String[] comm = message.split(COMMAND_TRIGGER);
		log.log(Level.INFO, message);
		String part1 = comm[1];
		String[] comm2 = part1.split(" ");
		String command = comm2[0];
		if (command != null) {
		    command = command.toLowerCase();
		}
		String roomName;
		String targetUserName;
		switch (command) {
		case CREATE_ROOM:
		    roomName = comm2[1];
		    if (server.createNewRoom(roomName)) {
			joinRoom(roomName, client);
		    }
		    wasCommand = true;
		    break;
		case JOIN_ROOM:
		    roomName = comm2[1];
		    joinRoom(roomName, client);
		    wasCommand = true;
		    break;
		case ROLL:
			sendRoll();
			wasCommand = true;
			break;
		case FLIP:
			sendFlip();
			wasCommand = true;
			break;
		case MUTE:
			targetUserName = comm2[1];
			client.muteUser(targetUserName);
			wasCommand = true;
			break;
		case UNMUTE:
			targetUserName = comm2[1];
			client.unmuteUser(targetUserName);
			wasCommand = true;
			break;
		}
	    }
	    /*
	    if (message.indexOf(PRIVATE_MSG_TRIGGER) == 0) {
	    	String[] s = message.split(" ", 2);
	    	String targetUserName = s[0].substring(1);
	    	String privMsg = s[1];
	    	ServerThread receiver = null;
	    	
	    	//wasCommand = true;
	    	
	    	//somehow get the client from clients based on clientname
	    	//then call sendPrivateMessage and pray it works
	    	
	    	Iterator<ServerThread> iter = clients.iterator();
	    	while (iter.hasNext()) {
	    	    ServerThread c = iter.next();
	    	    if (c.getClientName().equals(targetUserName)) {
	    	    	receiver = c;
	    	    }
	    	}
	    	//sendPrivateMessage(client, receiver, privMsg);
	    }
	    */
	}
	catch (Exception e) {
	    e.printStackTrace();
	}
	return wasCommand;
    }

    // TODO changed from string to ServerThread
    protected void sendConnectionStatus(ServerThread client, boolean isConnect, String message) {
	Iterator<ServerThread> iter = clients.iterator();
	while (iter.hasNext()) {
	    ServerThread c = iter.next();
	    boolean messageSent = c.sendConnectionStatus(client.getClientName(), isConnect, message);
	    if (!messageSent) {
		iter.remove();
		log.log(Level.INFO, "Removed client " + c.getId());
	    }
	}
    }

    /***
     * Takes a sender and a message and broadcasts the message to all clients in
     * this room. Client is mostly passed for command purposes but we can also use
     * it to extract other client info.
     * 
     * @param sender  The client sending the message
     * @param message The message to broadcast inside the room
     */
    
    protected void sendMessage(ServerThread sender, String message) {
    //commented b/c not really accurate message?
	//log.log(Level.INFO, getName() + ": Sending message to " + clients.size() + " clients");
    if (processCommands(message, sender)) {
	    // it was a command, don't broadcast
	    return;
	}
	
    //private messages handled here sorta
    //TODO: muting doesnt work for this yet
	boolean priv = false;
	
	if (message.indexOf(PRIVATE_MSG_TRIGGER) == 0) {
    	String[] s = message.split(" ", 2);
    	String targetUserName = s[0].substring(1);
    	String privMsg = s[1];
    	ServerThread receiver = null;
    	
    	//somehow get the client from clients based on clientname
    	//then call sendPrivateMessage and pray it works
    	
    	Iterator<ServerThread> iter = clients.iterator();
    	while (iter.hasNext()) {
    	    ServerThread c = iter.next();
    	    if (c.getClientName().equals(targetUserName)) {
    	    	receiver = c;
    	    }
    	}
        priv = sendPrivateMessage(sender, receiver, privMsg);
	}

	if(!priv) {
		Iterator<ServerThread> iter = clients.iterator();
		while (iter.hasNext()) {
		    ServerThread client = iter.next();
		    if(!client.getMuteList().contains(sender.getClientName())) {
			    boolean messageSent = client.send(sender.getClientName(), message);
			    if (!messageSent) {
				iter.remove();
				log.log(Level.INFO, "Removed client " + client.getId());
			    }
		    }
		}
	}
    }
    
    protected boolean sendPrivateMessage(ServerThread sender, ServerThread receiver, String message) {
    	log.log(Level.INFO, getName() + ": " + sender.getClientName() + " sending message to " + receiver.getClientName());
    	boolean messageSent = sender.send("to " + receiver.getClientName() + "[privately]", message);
    	if(!messageSent) {
    		log.log(Level.INFO, "Something went wrong. Uh oh.");
    		return false;
    	}
    	messageSent = receiver.send("from " + sender.getClientName() + "[privately]", message);
    	if(!messageSent) {
    		log.log(Level.INFO, "Something went wrong. Uh oh.");
    		return false;
    	}
    	return true;
    }
   
    //could combine sendFlip and sendRoll into one function...?
    protected void sendFlip() {
    	String result = "";
    	int randInt = rand.nextInt(2);
    	
    	if (randInt == 0) {
    		result = "heads";
    	} else if(randInt == 1) {
    		result = "tails";
    	}
    	
    	Iterator<ServerThread> iter = clients.iterator();
    	while (iter.hasNext()) {
    	    ServerThread client = iter.next();
    	    boolean messageSent = client.send("Flip result", result);
    	    if (!messageSent) {
    		iter.remove();
    		log.log(Level.INFO, "Removed client " + client.getId());
    	    }
    	}
    	
    }
    
    protected void sendRoll() {
    	//d6
    	int randInt = rand.nextInt(6) + 1;

    	String result = ""+randInt;
    	
    	Iterator<ServerThread> iter = clients.iterator();
    	while (iter.hasNext()) {
    	    ServerThread client = iter.next();
    	    boolean messageSent = client.send("Roll result", result);
    	    if (!messageSent) {
    		iter.remove();
    		log.log(Level.INFO, "Removed client " + client.getId());
    	    }
    	}
    }
    

    /***
     * Will attempt to migrate any remaining clients to the Lobby room. Will then
     * set references to null and should be eligible for garbage collection
     */
    @Override
    public void close() throws Exception {
	int clientCount = clients.size();
	if (clientCount > 0) {
	    log.log(Level.INFO, "Migrating " + clients.size() + " to Lobby");
	    Iterator<ServerThread> iter = clients.iterator();
	    Room lobby = server.getLobby();
	    while (iter.hasNext()) {
		ServerThread client = iter.next();
		lobby.addClient(client);
		iter.remove();
	    }
	    log.log(Level.INFO, "Done Migrating " + clients.size() + " to Lobby");
	}
	server.cleanupRoom(this);
	name = null;
	// should be eligible for garbage collection now
    }

}