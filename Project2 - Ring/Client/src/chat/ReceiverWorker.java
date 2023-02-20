package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

import message.MessageTypes;
import message.Message;


/*
 * PROGRESS NOTE:
 *  TODO: complete JOIN, SHUTDOWN, NOTE and SHUTDOWN_ALL cases for ring      
 */
public class ReceiverWorker extends Thread implements MessageTypes {
	//init variables
	Socket clientConnection = null;
	ObjectInputStream readFromNet = null;
	ObjectOutputStream writeToNet = null;
	Object[] messageList = null;
	Message origMessage, behindConnInfo;
	
	//constructor 
	public ReceiverWorker(Socket clientConnection) {
		try {
			this.clientConnection = clientConnection;
			readFromNet = new ObjectInputStream(clientConnection.getInputStream());
			writeToNet = new ObjectOutputStream(clientConnection.getOutputStream());
		}
		catch (IOException ex) {
			Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
					"Could not open object streams with [ReceiverWorker.run]");
		}
	}
	
	
	
	
	//threads start here
	public void run() {
		try {
			//read message 
			messageList = readFromNet.readObject();
			
			behindConnInfo = (Message) messageList[0];
			origMessage = (Message) messageList[1];
		}
		catch ( IOException | ClassNotFoundException ex) {
			Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
					"Message could not be read from [RecieverWorker.run]", ex);
			
			//exit
			System.exit(1);
		}
		
		//switch over different message types
		switch (origMessage.getType()) {
			
			//if shutdown close connection and exit
			case LEAVE:
			case SHUTDOWN:
				System.out.println("Received shutdown from client behind me");
				try {
					//check if conn info of the person leaving matches conn info of person i am pointing to
					
						//correct conn info of person I am pointing to to conn info of the person ahead of person leaving
					
					//otherwise
					
						//forward message around the ring
				}
				catch (IOException ex) {
					System.out.println("Error occured upon exit, exiting anyway");	
				}
				System.exit(0);
				break;
				
			//if note display note
			case NOTE:
				try {
					// check if my conn info doesn't match conn info of origMessage
						
						//print message
						System.out.println((String) orig_message.getContent());
						// forward message around the ring
						
					//otherwise assume I am the orginal sender and stop forwarding
						
					//close conn
					clientConnection.close();
				}
				catch (IOException ex){
					Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
							"message could not be read", ex);
				}
				break;
				
			case JOIN:
				//if behindConnInfo !equal my conn info
				
					//if origMessage equals my pointer
				
						// set my pointer to behindConnInfo
				
					//forward message
				
				//otherwise	
					
					//join is complete
				
			case SHUTDOWN_ALL:
				//chcek if my conn info doesn't match the conn info of the orig message
	
					//forward shutdown message around the ring
					
				//close connection
				clientConnection.close();
				
			default:
				//this code will not run
		}
	}

}
