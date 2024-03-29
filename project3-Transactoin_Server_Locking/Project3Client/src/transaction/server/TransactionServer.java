package transaction.server;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Properties;

import transaction.client.NodeInfo;
import transaction.client.TransactionClient;
import transaction.server.account.AccountManager;
import transaction.server.lock.LockManager;
import transaction.server.transaction.TransactionManager;
import transaction.server.transaction.TransactionManager.TransactionManagerWorker;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Properties;
import utils.PropertyHandler;

/*
 * Progress Note: 
 * 		Completed
 */

public class TransactionServer extends Thread{

	//public static Object accountManager;
	public static boolean transactionView;
	ServerSocket recieverSocket = null;
	
	static AccountManager accountManager = null;
	public static LockManager lockManager = null;
	
	static TransactionManager transactionManager = null;
	static int messageCount = 0;
	int serverPort = 0;
	String serverIP = null;
	
	NodeInfo myNodeInfo = null;
	NodeInfo serverNodeInfo = null;
	
	Socket client = null;
	
	int startingBal = -1;
	int  numAccounts = 0;
	
	public TransactionServer(String propertiesFile) 
	{
		
		//read config
		PropertyHandler properties = null;
		//get properties
		try {
			properties = new PropertyHandler(propertiesFile);
		}
		catch(IOException ex) {
			Logger.getLogger(TransactionServer.class.getName()).log(Level.SEVERE, 
					"error accessing properties", ex );
			System.exit(1);
		}
		
		try {
			serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));
		}
		catch (NumberFormatException ex ) {
			Logger.getLogger(TransactionServer.class.getName()).log(Level.SEVERE, 
					"error accessing server port number", ex );
			System.exit(1);
		}
		
		serverIP = properties.getProperty("SERVER_IP");
		if (serverIP == null) {
			Logger.getLogger(TransactionServer.class.getName()).log(Level.SEVERE, 
					"error accessing server IP");
			System.exit(1);
		}
		//create server
		if (serverPort != 0 && serverIP != null) {
			 serverNodeInfo = new NodeInfo(this.serverIP, this.serverPort);
		}
		
		startingBal = Integer.parseInt(properties.getProperty("STARTING_BAL"));
		if (startingBal == -1) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing starting bal");
			System.exit(1);
		}
		numAccounts =  Integer.parseInt(properties.getProperty("NUM_ACCOUNTS") );
		if (numAccounts == 0) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing num accounts");
			System.exit(1);
		}
		
		try {
			recieverSocket = new ServerSocket(serverPort);
		}
		catch(IOException ex) {
			System.out.println("error creating socket");
		}
		
		accountManager = new AccountManager(numAccounts, startingBal);
		lockManager = new LockManager();
		transactionManager = new TransactionManager(accountManager, lockManager);
	}
	public void run()
	{
		while(true) {
			try {
				
				client = recieverSocket.accept();
				transactionManager.runTransaction(client);
			}
			catch(IOException ex) {
				System.err.println("Client is not accepted");
			}
		}	
	}
	public static void main(String args[])
	{
		String propertiesFile = null;
		try {
			propertiesFile = args[0];
		}
		catch( ArrayIndexOutOfBoundsException ex) {
			propertiesFile = "config/server.properties";
		}
		//implement run function
		(new TransactionServer(propertiesFile)).run();
	}
	public static int getMessageCount() {
		// TODO Auto-generated method stub
		return messageCount;
	}
	public static void shutDown() {
		// TODO Auto-generated method stub
		
	}
}
