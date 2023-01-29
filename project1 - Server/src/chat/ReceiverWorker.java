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
 * Complete
 */
public class ReceiverWorker extends Thread implements MessageTypes {
	//init variables
	Socket serverConnection = null;
	ObjectInputStream readFromNet = null;
	ObjectOutputStream writeToNet = null;
	Message message;
	
	//constructor 
	public ReceiverWorker(Socket serverConnection) {
		try {
			readFromNet = new ObjectInputStream(serverConnection.getInputStream());
			writeToNet = new ObjectOutputStream(serverConnection.getOutputStream());
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
			message = (Message)readFromNet.readObject();
		}
		catch ( IOException | ClassNotFoundException ex) {
			Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
					"Message could not be read from [RecieverWorker.run]", ex);
			
			//exit
			System.exit(1);
		}
		
		//switch over different message types
		switch (message.getType()) {
			
			//if shutdown close connection and exit
			case SHUTDOWN:
				System.out.println("Received shutdown from server");
				try {
					serverConnection.close();
				}
				catch (IOException ex) {
					System.out.println("Error occured upon exit, exiting anyway");	
				}
				System.exit(0);
				break;
				
			//if note display note
			case NOTE:
				System.out.println((String) message.getContent());
				try {
					serverConnection.close();
				}
				catch (IOException ex){
					Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
							"message could not be read", ex);
				}
				break;
				
			default:
				//this code will not run
		}
	}

}
