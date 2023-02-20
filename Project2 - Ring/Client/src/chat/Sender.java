package chat;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import message.MessageTypes;
import message.Message;

/*
 * PROGRESS NOTE:
 * TODO: reformat sending as a list of [my_conn_info, orig_conn_info]
 */
public class Sender extends Thread implements MessageTypes {
	
	//init variables
	Socket serverConnection = null;
	
	ObjectInputStream readFromNet = null;
	ObjectOutputStream writeToNet = null;
	Scanner userInput = new Scanner(System.in);
	String inputLine = null;
	boolean joined;
	
	//constructor
	public Sender() {
		userInput = new Scanner(System.in);
		joined = false;
		
	}
	
	public void run() {
		
		while(true) {
			//read user input 
			inputLine = userInput.nextLine();
			
			//check start for keywords in messageTypes
			/*if JOIN check if already connected to chat if not, get 
			  connectivity info from user and send a join request to server */
			if (inputLine.startsWith("JOIN")) {
				if (joined) {
					System.err.println("You have already joined the chat");
					continue;
				}
				//get server info
				String[] connectivityInfo = inputLine.split("[ ]+");
				
				try {
					ChatClient.serverNodeInfo = 
							new NodeInfo(connectivityInfo[1], Integer.parseInt(connectivityInfo[2]));
					System.out.println("Server node info created");
				}
				catch (ArrayIndexOutOfBoundsException ex) {
					//defaults set  just don't crash
					//System.err.println("error");
				}
				
				//sanity check
				if (ChatClient.serverNodeInfo == null) {
					System.err.println("No server connectivity info provided");
					continue;
				}
				//info valid connect to server
				try {
					serverConnection = new Socket( ChatClient.serverNodeInfo.getIp(),
							               ChatClient.serverNodeInfo.getPort());
					readFromNet = new ObjectInputStream(serverConnection.getInputStream());
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
					
					//request to join server 
					NodeInfo obj1 = ChatClient.myNodeInfo; //m
					writeToNet.writeObject( new Message(JOIN, obj1)); //m
					//writeToNet.writeObject( new Message(JOIN, ChatClient.myNodeInfo));
					
					//close connection
					serverConnection.close();
				}
				catch (IOException ex) {
					Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
							"Error with sending join request");
					System.out.println(ex);
					continue;
				}
				joined = true;
				
				System.out.println("chat joined successfully");
				
			}
			/*upon a leave command check if connection exists if not send leave 
			request */
			else if (inputLine.startsWith("LEAVE")) {
				if (!joined) {
					System.err.println("You have not joined a chat yet");
					continue;
				}
				//leave server 
				try {
					//open connection to server
					serverConnection = new Socket( ChatClient.serverNodeInfo.getIp(),
				               ChatClient.serverNodeInfo.getPort()); 
					readFromNet = new ObjectInputStream(serverConnection.getInputStream());
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
					
					//request to leave server 
					
					writeToNet.writeObject( new Message(LEAVE,ChatClient.myNodeInfo));
					
					//close connection
					serverConnection.close();
				}
				catch (IOException ex) {
					Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
							"Error with sending leave request");
					continue;
				}
			}
			/*upon a shutdown_all command check if connection exists if not send
			  shutdown_all request */
			else if (inputLine.startsWith("SHUTDOWN_ALL")) {
				if (!joined) {
					System.err.println("You have not joined a chat yet");
					continue;
				}
				//shutdown all server connections 
				try {
					//open connection to server
					serverConnection = new Socket( ChatClient.serverNodeInfo.getIp(),
				               ChatClient.serverNodeInfo.getPort()); 
					readFromNet = new ObjectInputStream(serverConnection.getInputStream());
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
					
					//request to shutdown server
					
					writeToNet.writeObject( new Message(SHUTDOWN_ALL,ChatClient.myNodeInfo));
					
					//close connection
					serverConnection.close();
				}
				catch (IOException ex) {
					Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
							"Error with sending shutdown_all request");
					continue;
				}
			}
			/*upon a shutdown command check if connection exists if not send 
			  shutdown request */
			else if (inputLine.startsWith("SHUTDOWN")) {
				if (!joined) {
					System.err.println("You have not joined a chat yet");
					continue;
				}
				//shutdown personal server connection
				try {
					//open connection to server
					serverConnection = new Socket( ChatClient.serverNodeInfo.getIp(),
				               ChatClient.serverNodeInfo.getPort()); 
					readFromNet = new ObjectInputStream(serverConnection.getInputStream());
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
					
					//request to leave server
					
					writeToNet.writeObject( new Message(SHUTDOWN,ChatClient.myNodeInfo));
					
					//close connection
					serverConnection.close();
				}
				catch (IOException ex) {
					Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
							"Error with sending SHUTDOWN request");
					continue;
				}
			}
			/*if none of the above then the input is treated as a note */
			else {
				if (!joined) {
					System.err.println("You have not joined a chat yet");
					continue;
				}
				try {
					//open connection to server
					serverConnection = new Socket( ChatClient.serverNodeInfo.getIp(),
				               ChatClient.serverNodeInfo.getPort()); 
					readFromNet = new ObjectInputStream(serverConnection.getInputStream());
					writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
					
					String name_app = ChatClient.myNodeInfo.getName();
					writeToNet.writeObject( new Message(NOTE, name_app+":"+inputLine));
					
					//close connection
					serverConnection.close();
					
				}
				catch (IOException ex) {
					Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
							"Error with sending note");
					continue;
				}
			}
		}
	}
	

}
