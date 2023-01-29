package chat;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import message.Message;
import message.MessageTypes;


/*
 * PROGRESS NOTE:
 * Class is complete besides line 109 
 */
public class ChatServerWorker extends Thread implements MessageTypes {
	//init variables
	Socket chatConnection;
		
	public ChatServerWorker( Socket chatConnection) {
		this.chatConnection = chatConnection;
		
	}

	//threads start here
	public void run() {
		//init variables
		NodeInfo participantInfo = null;
		Iterator <NodeInfo> participantsIterator;
		ObjectInputStream readFromNet = null;
		ObjectOutputStream writeToNet = null;
		Message message = null;
		
		try {
			writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
			readFromNet = new ObjectInputStream(chatConnection.getInputStream());
			
			//get message 
			message = (Message) readFromNet.readObject();
		}
		catch (IOException | ClassNotFoundException ex) {
			Logger.getLogger(ReceiverWorker.class.getName()).log(Level.SEVERE,
					"Could not open object streams with [ChatServerWorker.run]");
		}
		//get message from input stream
	
		//utilize switch statements for cases
		switch(message.getType()) {
		
			//join
			case JOIN:
				//get participants Node info 
				NodeInfo joiningParticipantNodeInfo = (NodeInfo) message.getContent();
				
				//add to list
				ChatServer.participants.add(joiningParticipantNodeInfo);
				
				//display participants
				System.out.print(joiningParticipantNodeInfo.getName() + " joined. All current participants: ");
				
				//loop over participants and display names
				participantsIterator = ChatServer.participants.iterator();
				while(participantsIterator.hasNext()) {
					participantInfo = participantsIterator.next();
					System.out.print(participantInfo.name + " ");
				}
				System.out.println();
				break;
			
			//leave and shutdown are same
			case LEAVE:
			case SHUTDOWN:
				System.out.println("recieved a request for a client to exit the chat");
				NodeInfo leavingParticipantInfo = (NodeInfo) message.getContent();
				if (ChatServer.participants.remove(leavingParticipantInfo)) {
					System.err.println(leavingParticipantInfo.getName() + " removed");
				}
				else {
					System.err.println(leavingParticipantInfo.getName() + " not found");
				}
				System.out.print(leavingParticipantInfo.getName() + " left. Heres who remains:");
				//loop over participants and display names
				participantsIterator = ChatServer.participants.iterator();
				while(participantsIterator.hasNext()) {
					participantInfo = participantsIterator.next();
					System.out.print(participantInfo.name + " ");
				}
				System.out.println();
				break;
				
			
			//shutdown all
			case SHUTDOWN_ALL:
				System.out.println("recieved shutdown all signal from client");
				//loop participants
				participantsIterator = ChatServer.participants.iterator();
				while(participantsIterator.hasNext()) {
					//get participant info
					participantInfo = participantsIterator.next();
					//try to connect to one client at a time
					try {
						chatConnection = new Socket( participantInfo.getIp(),
								 participantInfo.getPort()); 
						//write message of type note to client
						/* QUESTION ON LINE BELOW IS CORRECT*/
						writeToNet.writeObject( new Message( SHUTDOWN, (NodeInfo) message.getContent() ) );
				
						//close connection
						chatConnection.close();
					}
				
					//catch IOE exception
					catch (IOException ex) {
						Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
								"Error with sending note to a participant");
						continue;
					}
				}
				
			
			//note
			case NOTE:
				//display note 
				System.out.println( (String) message.getContent());
				
				//loop participants
				participantsIterator = ChatServer.participants.iterator();
				while(participantsIterator.hasNext()) {
					//get participant info
					participantInfo = participantsIterator.next();
					//try to connect to one client at a time
					try {
						chatConnection = new Socket( participantInfo.getIp(),
								 participantInfo.getPort()); 
						//write message of type note to client
						writeToNet.writeObject( message );
				
						//close connection
						chatConnection.close();
					}
				
					//catch IOE exception
					catch (IOException ex) {
						Logger.getLogger(Sender.class.getName()).log(Level.SEVERE,
								"Error with sending note to a participant");
						continue;
					}
					
					
				}
				System.out.println("Message sent to each participants successfully");
		}
				
			
	}
	
}

