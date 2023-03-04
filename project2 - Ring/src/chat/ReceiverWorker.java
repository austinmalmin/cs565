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
	NodeInfo myPointer = null;
	NodeInfo clientAhead = null;
	Message origSenderMessage = null;
	NodeInfo origSenderInfo = null;
	
	//constructor 
	public ReceiverWorker(NodeInfo myPointer, Socket clientConnection) {
		try {
			this.myPointer = myPointer;
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
			messageList = (Object[]) readFromNet.readObject();
			
			origMessage = (Message) messageList[0];
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
			case SHUTDOWN:
			case LEAVE:
				clientAhead = (NodeInfo) ((Message) messageList[1]).getContent();
				origSenderInfo = (NodeInfo) origMessage.getContent();
				System.out.println(origSenderInfo.getName() + " has left");
				//check if conn info of the person leaving matches conn info of person i am pointing to
				if( ChatClient.knownClientInfo.equals(origSenderInfo)) {
					//correct conn info of person I am pointing to to conn info of the person ahead of person leaving
					ChatClient.knownClientInfo = clientAhead;
				}
				//otherwise
				else
					try {
						//forward message
						Socket myPointerConn = new Socket( ChatClient.knownClientInfo.getIp(),
								ChatClient.knownClientInfo.getPort()); 
						writeToNet = new ObjectOutputStream(myPointerConn.getOutputStream());
						readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
						writeToNet.writeObject(messageList);
						myPointerConn.close();
					}
					catch (IOException ex) {
						System.out.println("Error occured upon exit, exiting anyway");	
					}
				break;
				
			//if note display note
			case NOTE:
				try {
					origSenderMessage = (Message) messageList[1];
					origSenderInfo = (NodeInfo) origSenderMessage.getContent();
					if( ChatClient.myNodeInfo.equals(origSenderInfo)) {
					}
					else{
						//print message
						System.out.println(origMessage.getContent());

						// forward message around the ring
						Socket myPointerConn = new Socket( ChatClient.knownClientInfo.getIp(),
								ChatClient.knownClientInfo.getPort()); 
						writeToNet = new ObjectOutputStream(myPointerConn.getOutputStream());
						readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
						writeToNet.writeObject(messageList);
						myPointerConn.close();
					}
						
					//close conn
					clientConnection.close();
				}
				catch (IOException ex){
					Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
							"message could not be read", ex);
				}
				break;
				
			case JOIN:
				origSenderInfo = (NodeInfo) origMessage.getContent();
				clientAhead = (NodeInfo) ((Message) messageList[1]).getContent();
				//if behindConnInfo !equal my conn info
				if(clientAhead.equals(ChatClient.knownClientInfo)) {
					//i repair my pointer to orig sender info
					ChatClient.knownClientInfo = origSenderInfo;
				}
					//otherwise	
				else {
					try {
						//forward message
						Socket myPointerConn = new Socket( ChatClient.knownClientInfo.getIp(),
								ChatClient.knownClientInfo.getPort()); 
						writeToNet = new ObjectOutputStream(myPointerConn.getOutputStream());
						readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
						writeToNet.writeObject(messageList);
						myPointerConn.close();
					}
					catch (IOException ex){
						Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
								"message could not be read", ex);
					}
				}
				break;
				
			case SHUTDOWN_ALL:
				origSenderInfo = (NodeInfo) origMessage.getContent();
				//chcek if my pointer  matches the conn info of the orig sender
				if(  ChatClient.knownClientInfo.equals(origSenderInfo)) {
					System.out.println(origSenderInfo.getName() + " has called for shutdown and it has completed");
					System.exit(0);
				}
				else {
					System.out.println(origSenderInfo.getName() + " has called for shutdown all, forwarding message");
					//forward message
					try {
						Socket myPointerConn = new Socket( ChatClient.knownClientInfo.getIp(),
								ChatClient.knownClientInfo.getPort()); 
						writeToNet = new ObjectOutputStream(myPointerConn.getOutputStream());
						readFromNet = new ObjectInputStream(myPointerConn.getInputStream());
						writeToNet.writeObject(messageList);
						myPointerConn.close();
					}
					catch (IOException ex){
						Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
								"message could not be read", ex);
					}
				}


				//exit 
				System.out.println("Shutting down");
				System.exit(0);
				
			default:
				//this code will not run
		}
	}

}
