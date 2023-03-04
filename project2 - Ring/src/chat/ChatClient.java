package chat;

import java.util.Properties;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.PropertyHandler;
import utils.NetworkUtilities;

/*
 * PROGRESS NOTES:
 * this class is complete
 * */

public class ChatClient implements Runnable {

	//init variables
	static Sender sender = null;
	static Receiver receiver = null;
	
	public static NodeInfo myNodeInfo = null;
	public static NodeInfo knownClientInfo = null;
	
	int portNumber = 0;
	int knownClientPort = 0;
	String name = null;
	String knownClientIP = null;
	NodeInfo myPointer = null;
	
	//constructor
	public ChatClient(String propertiesFile) {
		System.out.println("chat client is being made");
		Properties properties = null;
		//get properties
		try {
			properties = new PropertyHandler(propertiesFile);
		}
		catch( IOException ex ) {
			Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, 
					"error accessing properties", ex );
			System.exit(1);
		}
		
		try {
			portNumber = Integer.parseInt(properties.getProperty("MY_PORT"));
		}
		catch (NumberFormatException ex) {
			Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, 
					"error accessing port number", ex );
			System.exit(1);
		}
		name = properties.getProperty("MY_NAME");
		if (name == null) {
			Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, 
					"error accessing name");
			System.exit(1);
		}
		
		myNodeInfo = new NodeInfo(NetworkUtilities.getMyIP(), portNumber, name);
		
		try {
			knownClientPort = Integer.parseInt(properties.getProperty("KNOWN_CLIENT_PORT"));
		}
		catch (NumberFormatException ex ) {
			Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, 
					"error accessing known client port number", ex );
			System.exit(1);
		}
		
		knownClientIP = properties.getProperty("KNOWN_CLIENT_IP");
		if (knownClientIP == null) {
			Logger.getLogger(ChatClient.class.getName()).log(Level.SEVERE, 
					"error accessing known client IP");
			System.exit(1);
		}
		//create server
		if (knownClientPort != 0 && knownClientIP != null) {
			knownClientInfo = new NodeInfo( knownClientIP, knownClientPort);
			myPointer = knownClientInfo;
		}
		
	}
	

	public void run() {
		//start receiver
		(receiver = new Receiver(knownClientInfo)).start();
		//now start sender
		(sender = new Sender(knownClientInfo)).start();
		
	}
	
	public static void main( String[] args) {
		String propertiesFile = null;
		
		try {
			propertiesFile = args[0];
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			propertiesFile = "config/ChatNodeDefaults.properties";
		}
		
		(new ChatClient(propertiesFile)).run();
		
	}
}