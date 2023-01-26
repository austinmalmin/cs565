package chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

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

}
