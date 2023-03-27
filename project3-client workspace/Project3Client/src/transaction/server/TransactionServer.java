package transaction.server;
//package utils;

import java.net.ServerSocket;
import java.util.*;
import java.util.Properties;

import transaction.server.transaction.TransactionManager;

import java.io.IOException;
import java.lang.System.Logger;
import java.lang.System.Logger.Level;
import java.util.Properties;
import utils.PropertyHandler;

/*
 * Progress Note: 
 * 		Class is in working progress, there will be some errors at compile time.
 */

public class TransactionServer {

	public static Object accountManager;
	public static boolean transactionView;
	ServerSocket serverSocket = null;
	
	static int messageCount = 0;
	int serverPort = 0;
	String serverIP = null;
	
	public TransactionServer(String propertiesFile) 
	{
		
		NodeInfo myNodeInfo = null;
		NodeInfo serverNodeInfo = null;
		
		
		
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
					"error accessing server IP", ex);
			System.exit(1);
		}
		//create server
		if (serverPort != 0 && serverIP != null) {
			 serverNodeInfo = new NodeInfo(this.serverIP, this.serverPort);
		}
		
		
		try {
			serverSocket = new ServerSocket(serverPort);
		}
		catch(IOException ex) {
			System.out.println("error creating socket");
		}
	}
	public void run()
	{
		while(true){
			try {
				(new TransactionManager(serverSocket.accept())); // write the start
			}
			catch(IOException e) {
				//
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
		(new ChatServer(propertiesFile).run());
	}
	public static int getMessageCount() {
		// TODO Auto-generated method stub
		return messageCount;
	}
}
