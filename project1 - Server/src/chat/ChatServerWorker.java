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
			Logger.getLogger(ChatServerWorker.class.getName()).log(Level.SEVERE,
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
				if (ChatServer.participants.remove( leavingParticipantInfo ) ) {
					System.out.println(leavingParticipantInfo.getName() + " removed");
					System.out.print(leavingParticipantInfo.getName() + " left. Heres who remains:");
					//loop over participants and display names
					participantsIterator = ChatServer.participants.iterator();
					while(participantsIterator.hasNext()) {
						participantInfo = participantsIterator.next();
						
						System.out.print(participantInfo.name + " ");
					}
					System.out.println();
					try {
						chatConnection = new Socket( leavingParticipantInfo.getIp(),
								leavingParticipantInfo.getPort());
						writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
						readFromNet = new ObjectInputStream(chatConnection.getInputStream());
						writeToNet.writeObject( new Message(SHUTDOWN, message.getContent()) );
						
					} 
					catch (IOException e) {
						System.err.println("error conneting to leaving cleint");
					}
					
					
				}
				else {
					System.err.println(leavingParticipantInfo.getName() + " not found");
					participantsIterator = ChatServer.participants.iterator();
					while(participantsIterator.hasNext()) {
						participantInfo = participantsIterator.next();
					}
				}
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
						writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
						readFromNet = new ObjectInputStream(chatConnection.getInputStream());
						//write message of type note to client
						writeToNet.writeObject( new Message(SHUTDOWN, message.getContent()) );
				
						//close connection
						chatConnection.close();
					}
				
					//catch IOE exception
					catch (IOException ex) {
						Logger.getLogger(ChatServerWorker.class.getName()).log(Level.SEVERE,
								"Error with sending note to a participant");
						continue;
					}
				}
				break;
				
			
			//note
			case NOTE:
				//display note 
				System.out.println( (String) message.getContent());
				
				//loop participants
				System.out.println("about to enter loop");
				participantsIterator = ChatServer.participants.iterator();
				while(participantsIterator.hasNext()) {
					System.out.println("entered loop");
					//get participant info
					participantInfo = participantsIterator.next();
					//try to connect to one client at a time
					try {
						//System.out.println("opening conn");
						chatConnection = new Socket( participantInfo.getIp(),
								 participantInfo.getPort()); 
						//System.out.println("conn est.");
						writeToNet = new ObjectOutputStream(chatConnection.getOutputStream());
						//System.out.println("writeToNet est");
						//readFromNet = new ObjectInputStream(chatConnection.getInputStream());
						//System.out.println("readFromNet est");
						//write message of type note to client
						writeToNet.writeObject( message );
						writeToNet.flush();
						//close connection
						//System.out.println("trying to close conn on server side");
						chatConnection.close();
						//System.out.println("conn closed server side");
					}
				
					//catch IOE exception
					catch (IOException ex) {
						Logger.getLogger(ChatServerWorker.class.getName()).log(Level.SEVERE,
								"Error with sending note to a participant");
						//ex.printStackTrace();
						continue;
					}
					
					
				}
				System.out.println("Message sent to each participants successfully");
		}
				
			
	}
	
}

