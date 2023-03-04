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
	Socket myPointerConn = null;
	
	ObjectInputStream readFromNet = null;
	ObjectOutputStream writeToNet = null;
	Scanner userInput = new Scanner(System.in);
	String inputLine = null;
	boolean joined;
	NodeInfo myPointer = null;
	NodeInfo knownClientInfo = null;
	
	//constructor
	public Sender(NodeInfo knownChatClient) {
		this.knownClientInfo = knownChatClient;
		userInput = new Scanner(System.in);
		joined = ChatClient.myNodeInfo.equals(knownClientInfo);
		
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
					joined = true;
					continue;
				}
				else {
					//get server info
					String[] connectivityInfo = inputLine.split("[ ]+");
					
					try {
						System.out.println("Chat node info created");
					}
					catch (ArrayIndexOutOfBoundsException ex) {
						//defaults set  just don't crash
						//System.err.println("error");
					}
					
					//sanity check
					if (knownClientInfo == null) {
						System.err.println("No Chat Client connectivity info provided");
						continue;
					}
					//info valid connect to server
					try {
						myPointerConn = new Socket( knownClientInfo.getIp(),
								knownClientInfo.getPort());
						writeToNet = new ObjectOutputStream(myPointerConn.getOutputStream());
						readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
						
						//request to join server 
						Object[] senderArr = new Message[2];
						senderArr[0] = new Message(JOIN, ChatClient.myNodeInfo);
						senderArr[1] = new Message( JOIN, ChatClient.knownClientInfo);
						writeToNet.writeObject( senderArr );
						
						//close connection
						myPointerConn.close();
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
				
			}
			/*upon a leave command check if connection exists if not send leave 
			request */
			else if (inputLine.startsWith("LEAVE") || inputLine.startsWith("SHUTDOWN")) {
				if (!joined) {
					System.err.println("You have not joined a chat yet");
					continue;
				}
				//leave server 
				try {
					//open connection to server
					myPointerConn = new Socket( knownClientInfo.getIp(),
				               myPointerConn.getPort()); 
					writeToNet = new ObjectOutputStream(myPointerConn.getOutputStream());
					readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
					
					//request to leave server 
					Object[] senderArr = new Message[2];
					senderArr[0] = new Message(LEAVE, ChatClient.myNodeInfo);
					senderArr[1] = new Message( LEAVE, ChatClient.knownClientInfo);
					writeToNet.writeObject( senderArr );
					writeToNet.writeObject( new Message(LEAVE,ChatClient.myNodeInfo));
					
					//close connection
					myPointerConn.close();
				}
				catch (IOException ex) {
					Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
							"Error with sending leave request");
					continue;
				}
				System.exit(0);
			}
			/*upon a shutdown_all command check if connection exists if not send
			  shutdown_all request */
			else if (inputLine.startsWith("ALL")) {
				if (!joined) {
					System.err.println("You have not joined a chat yet");
					continue;
				}
				//shutdown all server connections 
				try {
					//open connection to server
					myPointerConn = new Socket( ChatClient.knownClientInfo.getIp(),
							ChatClient.knownClientInfo.getPort()); 
					writeToNet = new ObjectOutputStream(myPointerConn.getOutputStream());
					readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
					Object[] senderArr = new Message[2];
					senderArr[0] = new Message(SHUTDOWN_ALL, ChatClient.myNodeInfo);
					writeToNet.writeObject( senderArr );
					
					//close connection
					myPointerConn.close();
				}
				catch (IOException ex) {
					Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
							"Error with sending shutdown_all request");
					continue;
				}
				System.out.println("Processed shutdown all and shutting down");
				System.exit(0);
			}
			/*if none of the above then the input is treated as a note */
			else {
				if (!joined) {
					System.err.println("You have not joined a chat yet");
					continue;
				}
				try {
					//open connection to server
					System.out.println("in sender current known info is:");
					System.out.println(ChatClient.knownClientInfo.getIp() +":" + ChatClient.knownClientInfo.getPort());
					myPointerConn = new Socket( ChatClient.knownClientInfo.getIp(),
							ChatClient.knownClientInfo.getPort()); 
					//readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
					writeToNet = new ObjectOutputStream(myPointerConn.getOutputStream());
					readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
					String name_app = ChatClient.myNodeInfo.getName();
					Object[] senderArr = new Message[2];
					senderArr[0] = new Message(NOTE, name_app+":"+inputLine);
					senderArr[1] = new Message( NOTE, ChatClient.myNodeInfo);
					writeToNet.writeObject( senderArr );
					
					//close connection
					myPointerConn.close();
					System.out.println(name_app +":" + inputLine);
					
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