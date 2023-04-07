package transaction.client;

import java.util.Properties;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import utils.PropertyHandler;
import utils.NetworkUtilities;

//generate transactionServerProxy
public class TransactionClient 
{
	
	public static NodeInfo myNodeInfo = null;
	public static NodeInfo serverNodeInfo = null;
	
	int portNumber = 0;
	int serverPort = 0;
	String serverIP = null;
	int startingBal = -1;
	int  numAccounts = 0;
	
	
	public TransactionClient(String propertiesFile) 
	{
		//read config
		Properties properties = null;
		//get properties
		try {
			properties = new PropertyHandler(propertiesFile);
		}
		catch( IOException ex ) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing properties", ex );
			System.exit(1);
		}
		
		try {
			portNumber = Integer.parseInt(properties.getProperty("MY_PORT"));
		}
		catch (NumberFormatException ex) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing port number", ex );
			System.exit(1);
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
		
		myNodeInfo = new NodeInfo(NetworkUtilities.getMyIP(), portNumber);
		
		try {
			serverPort = Integer.parseInt(properties.getProperty("SERVER_PORT"));
		}
		catch (NumberFormatException ex ) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing server port number", ex );
			System.exit(1);
		}
		
		serverIP = properties.getProperty("SERVER_IP");
		if (serverIP == null) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing server IP");
			System.exit(1);
		}
		//create server
		if (serverPort != 0 && serverIP != null) {
			serverNodeInfo = new NodeInfo( serverIP, serverPort);
		}
		
	}
		
	public static void main( String[] args)
	{
		String propertiesFile = null;
		try {
			propertiesFile = args[0];
		}
		catch( ArrayIndexOutOfBoundsException ex) {
			propertiesFile = "config/tdefault.properties";
		}
		TransactionClient tclient = new TransactionClient(propertiesFile);
		
		
		
		System.out.println("startingBal : " + tclient.startingBal);
		System.out.println("numAccounts : " + tclient.numAccounts);
	}
}