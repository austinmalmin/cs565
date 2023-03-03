package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * PROGRESS NOTE: 
 * This class is complete
 */
public class Receiver extends Thread {

	static ServerSocket receiverSocket = null;
	static String userName = null;
	NodeInfo myPointer = null;
	
	//constructor
	public Receiver(NodeInfo myPointer) {
		try {
			this.myPointer = myPointer;
			receiverSocket = new ServerSocket(ChatClient.myNodeInfo.getPort());
			System.out.println("receiver socket created with receiver" +
			 "[Receiver.Receiver] and port " + ChatClient.myNodeInfo.getPort());
		}
		catch (IOException ex) {
			Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, 
					"Error creating socket", ex);
		}
		System.out.println(ChatClient.myNodeInfo.getName() + " listening on " + 
				ChatClient.myNodeInfo.getIp() + ":" + ChatClient.myNodeInfo.getPort());
	}
	
	public void run(NodeInfo myPointer) {
		while (true) {
			try {
				(new ReceiverWorker(myPointer, receiverSocket.accept())).start();
			}
			
			catch (IOException ex) {
				System.err.println("Client was not accepted with reciever: [Receiver.run]");
			}
		}
	}
}
