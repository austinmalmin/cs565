package transaction.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import transaction.comm.MessageTypes;
import transaction.server.lock.TransactionAbortedException;
import utils.PropertyHandler;
import utils.NetworkUtilities;


/*
 * Client is done
 */
public class TransactionClient implements MessageTypes
{
	public static final int NOT_FOUND = -1;
	
	public static NodeInfo myNodeInfo = null;
	public static NodeInfo serverNodeInfo = null;
	
	int portNumber = NOT_FOUND;
	int serverPort = NOT_FOUND;
	String serverIP = null;
	int startingBal = NOT_FOUND;
	int  numAccounts = NOT_FOUND;
	int total_transctions = NOT_FOUND;
	
	
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
		if (startingBal == NOT_FOUND) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing starting bal");
			System.exit(1);
		}
		
		numAccounts =  Integer.parseInt(properties.getProperty("NUM_ACCOUNTS") );
		if (numAccounts == NOT_FOUND) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing num accounts");
			System.exit(1);
		}
		
		total_transctions =  Integer.parseInt(properties.getProperty("TOTAL_TRANSACTIONS") );
		if (total_transctions == NOT_FOUND) {
			Logger.getLogger(TransactionClient.class.getName()).log(Level.SEVERE, 
					"error accessing total transctions");
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
		if (serverPort != NOT_FOUND && serverIP != null) {
			serverNodeInfo = new NodeInfo( serverIP, serverPort);
		}
		
	}
		
	public static void main( String[] args)
	{
		
		Random random = new Random();
		
		String propertiesFile = null;
		try {
			propertiesFile = args[0];
		}
		catch( ArrayIndexOutOfBoundsException ex) {
			propertiesFile = "config/tdefault.properties";
		}
		
		//Map that stores current transaction index and meta data for transaction
		/*
		 * [current transaction] , [1st_acc, 2nd_acc, balance change]
		 */
		Map<Integer, List<Integer>> aborted_transactionInfo = new HashMap<>();
		
		TransactionClient tclient = new TransactionClient(propertiesFile);
		
		int abortedIndex = 0;
		int status = TRANSACTION_ERROR;
		int successfullTransactions = 0;
		
		//This is the Transaction Loop//
		
		for(int current_transaction = 0; current_transaction < tclient.total_transctions; current_transaction++) {
			
			//System.out.println("Aborted Transaction in for loop TC"); 
			
			int balance_change  = random.nextInt(tclient.startingBal);
			int first_account  = random.nextInt(tclient.numAccounts);
			int second_account  = random.nextInt(tclient.numAccounts);
			
			TransactionServerProxy proxy = new TransactionServerProxy(tclient.serverIP, tclient.serverPort);
			int transactionId = proxy.openTransaction();
			
			try {
				
				int Bal1 = proxy.read(first_account, transactionId);
				int Bal2 = proxy.read(second_account, transactionId);
				
				Bal1 += balance_change;
				Bal2 -= balance_change;
				
				proxy.write(first_account, Bal1, transactionId);
				proxy.write(second_account, Bal2, transactionId);
				
				status = proxy.closeTransaction();
				successfullTransactions++;
			}
			catch(TransactionAbortedException te) {
				
				List<Integer> transactionInfo = new ArrayList<>();
				
				transactionInfo.add(first_account);
				transactionInfo.add(second_account);
				transactionInfo.add(balance_change);
				
				aborted_transactionInfo.put(abortedIndex, transactionInfo);
				abortedIndex++;
			}
		}
		
		System.out.println("size of abortedIndex: " + abortedIndex);
		
		//Re-Starting Aborted Transactions while there are no more transactions!
		int local_abortedIndex = 0;
		while(!aborted_transactionInfo.isEmpty()) {
			
			System.out.println("size of abortedIndex: " + abortedIndex);
			System.out.println("size of successfull Transactions: " + successfullTransactions);
			//System.out.println("Aborted Transaction in while loop TC");
			List<Integer> transInfo = (List<Integer>) aborted_transactionInfo.get(local_abortedIndex);
			int first_account  = transInfo.get(0);
			int second_account  = transInfo.get(1);
			int balance_change  = transInfo.get(2);
			
			TransactionServerProxy proxy = new TransactionServerProxy(tclient.serverIP, tclient.serverPort);
			
			int transactionId = proxy.openTransaction();
			
			try {
				
				int Bal1 = proxy.read(first_account, transactionId);
				int Bal2 = proxy.read(second_account, transactionId);
				
				Bal1 += balance_change;
				Bal2 -= balance_change;
				
				proxy.write(first_account, Bal1, transactionId);
				proxy.write(second_account, Bal2, transactionId);
			
			}
			catch(TransactionAbortedException te) {
				
				List<Integer> transactionInfo = new ArrayList<>();
				
				transactionInfo.add(first_account);
				transactionInfo.add(second_account);
				transactionInfo.add(balance_change);
				
				aborted_transactionInfo.put(abortedIndex, transactionInfo);
				abortedIndex++;
				
			}
			
			//removing aborted transaction at the end
			aborted_transactionInfo.remove(local_abortedIndex);
			local_abortedIndex++;
		}
		
		TransactionServerProxy proxy = new TransactionServerProxy(tclient.serverIP, tclient.serverPort);
		System.out.println("Expected sum is " + (tclient.startingBal * tclient.numAccounts) + "sum is " + proxy.getSum());
		
	}
}