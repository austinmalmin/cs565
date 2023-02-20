package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * PROGRESS NOTES:
 * Class is complete
 */

public class ChatServer implements Runnable {
	//init varibles 
	String propertiesFile;
	
	public static List<NodeInfo> participants = new ArrayList<>();
	
	//connection variables
	int port = 0;
	ServerSocket serverSocket = null;
	
	//constructor
	public ChatServer( String propertiesFile) {
		this.propertiesFile = propertiesFile;
		
		// SERVER Code Add-on Start
		try {
			serverSocket = new ServerSocket(8886); 
			System.out.println("receiver socket created with receiver" +
			 " 127.0.0.1 and port 8885");
		}
		catch (IOException ex) {
			//Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, 
					//"Error creating socket", ex);
			System.out.println("error");
		}
		// SERVER Code Add-on End
	}
	public void run() {
		//server loop
		while (true) {
			try {
				(new ChatServerWorker(serverSocket.accept())).start();
			}
			catch (IOException e) {
				System.out.println("[ChatServer.run] Warning: Error accepting client");
			}
		}
	}

	// Server main() Added Start
	public static void main( String[] args) {
		String propertiesFile = null;
		
		try {
			propertiesFile = args[0];
		}
		catch (ArrayIndexOutOfBoundsException ex) {
			propertiesFile = "config/ChatNodeDefaults.properties";
		}
		
		(new ChatServer(propertiesFile)).run();
		
	}
	//Server main() Added End
}
