package transaction.server.transaction;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java. net. Socket;
import java.util.ArrayList;
import java.util. HashMap;
import java.util. Iterator;
import java.util.Map;
import transaction.comm.Message;
import transaction.comm.MessageTypes;
import transaction.server.TransactionServer;
import transaction.server.account.AccountManager;
import transaction.server.lock.LockManager;
import transaction.server.lock.LockTypes;
import utils.TerminalColors;
//Add Colors once we format output

/*
 * Completed
 */

public class TransactionManager extends Thread implements MessageTypes, LockTypes, TerminalColors
{	
	
	private static int transactionIdCounter = 0;
	private static final ArrayList<Transaction> runningTransactions = new ArrayList<> ();
	private static final ArrayList<Transaction> abortedTransactions = new ArrayList<>();
	//private static final String OPEN_COLOR = null;
	//private static final String RESET_COLOR = null;
	private static int transactionNumberCounter = 0;
	
	Object[] content; 
	int accountNumber;
	int balance;
	int transactionId;
	boolean success = false;
	int  numCommitted = 0;
	
	static AccountManager accountManager = null;
	public static LockManager lockManager = null;
	
	
	private static final ArrayList<Transaction> committedTransactions = new ArrayList<> ();
	
	 public TransactionManager(AccountManager accountManager, LockManager lockManager) {
		 this.accountManager = accountManager;
		 this.lockManager = lockManager;
	 }
	
	public ArrayList<Transaction> getAbortedTransaction()
	{
		return abortedTransactions;
	}
	
	public synchronized void runTransaction(Socket client) 
	{
		(new TransactionManagerWorker(client)).start();
		
	}
	
	public ArrayList<Transaction> getRunningTransactions(){
		return runningTransactions;
	}
	
	public ArrayList<Transaction> getCommittedTransactions(){
		return committedTransactions;
	}
	
	public int getSum() {
		int sum=0;
		for(int i =0; i<accountManager.accountList.size(); i++) {
			sum += accountManager.accountList.get(i).getBalance();
		}
		return sum;
	}
	public class TransactionManagerWorker extends Thread
	{
		
		//private static final String COMMIT_COLOR = null;
		//private static final String READ_COLOR = null;
		//private static final String WRITE_COLOR = null;
		Socket client = null;
		ObjectInputStream readFromNet = null;
		ObjectOutputStream writeToNet = null;
		Message message = null;
		
		Transaction transaction = null;
		int accountNumber = 0;
		int balance = 0;
		
		
		boolean Keepgoing = true;
		
		private TransactionManagerWorker(Socket client)
		{
			this.client = client;
			
			try{
				this.readFromNet = new ObjectInputStream(client.getInputStream ());
				this.writeToNet = new ObjectOutputStream(client.getOutputStream());
			}
			catch(IOException e)
			{
				System.out.println(" [TransactionManagerWorker.run] Failed to open object streams"); 
				e.printStackTrace();
				System.exit(1);
			}
		}
	
	
	
	public void run()
	{
		
		while (Keepgoing)
		{
			Message message = null;
			// reading message
			try {
				
				//private ObjectOutputStream writeToNet = null;
				//private ObjectInputStream readFromNet = null;
				
				message = (Message) readFromNet.readObject();
			}
			catch(IOException | ClassNotFoundException e) 
			{
				System.out.println(" [TransactionManagerWorker. run] Message could not be read from object stream.");
				TransactionServer.shutDown();
				return;
			}
			
			Transaction transaction = null;
			switch (message.getType())
			{

				case OPEN_TRANSACTION:
					
					synchronized (runningTransactions)
					{
						// assign a new transaction ID, also pass in the last assigned transaction number
						// as to the latter, that number may refer to a (prior, non-overlapping) transaction that needed to be aborted
						
						System.out.println("Creating Transaction");
						
						transaction = new Transaction(++transactionIdCounter);
						
						runningTransactions.add(transaction);
					}
					try
					{
						writeToNet.writeObject(new Message(OPEN_TRANSACTION, transaction.getTransactionId()));
					}
					catch(IOException e)
					{
						System.err.println("[TransactionManagerWorker.run] OPEN_TRANSACTION #" + transaction.getTransactionId() + " - Error writing transactionID");
					}
					
					transaction.log("[TransactionManagerWorker.run] " + OPEN_COLOR + "OPEN_TRANSACTION" + RESET_COLOR + " #" + transaction.getTransactionId());
					System.out.println(OPEN_COLOR + " OPEN_TRANSACTION " + RESET_COLOR + " #" + transaction.getTransactionId());
					break;
					
					
				case CLOSE_TRANSACTION:
					
					TransactionServer.lockManager.unlock(transaction);
					
					runningTransactions.remove(transaction);
					committedTransactions.add(transaction);
					
					try {
						
						writeToNet.writeObject(new Message(TRANSACTION_COMMITTED, TRANSACTION_COMMITTED));
						
						readFromNet.close();
						writeToNet.close();
						client.close();
						
						Keepgoing = false;
					}
					catch(IOException e) {
						System.out.println("[TransactionManagerWorker.run] CLOSE_TRANSACTION - Error when closing the connection to client");
					}
					
					transaction.log("[TransactionManagerWorker.run] " + COMMIT_COLOR + "CLOSE_TRANSACTION" + RESET_COLOR + " #" +
							transaction.getTransactionId());
					System.out.println(COMMIT_COLOR + " CLOSE_TRANSACTION " + RESET_COLOR + " #" + transaction.getTransactionId());
					
					if(TransactionServer.transactionView) {
						System.out.println(transaction.getLog());
					}
					break;
					
				case WRITE_REQUEST:
				
					 //call AccountManager to acquire a WRITE Lock.
					content =  (Object[]) message.getContent();
					accountNumber = (Integer) content[0];
					balance = (Integer) content[1];
					transactionId = (int) content[2];
					
					for(int i=0; i<runningTransactions.size(); i++) {
						if(runningTransactions.get(i).getTransactionId() == transactionId) {
							transaction = runningTransactions.get(i);
							break;
						}
					}
	
					transaction.log("[TransactionManagerWorker.run] " + WRITE_COLOR + "WRITE_REQUEST" + RESET_COLOR + ">>>>>>>>> accountNumber being wrote from" + accountNumber );
					System.out.println(WRITE_COLOR + " WRITE_REQUEST " + RESET_COLOR + " #" + transaction.getTransactionId());
					
					success = TransactionServer.lockManager.setLock(transaction, WRITE);
					
					if(success) {
						accountManager.write(accountNumber, balance);
					
						try {
							System.out.println("sending write request response");
							writeToNet.writeObject(new Message(WRITE_REQUEST_RESPONSE, accountNumber));
						}
						catch(Exception e) {
							System.out.println("[TransactionManagerWorker.run] WRITE_REQUEST_RESPONSE - Error when writing the response to client");
						}
					}
					else
					{
						try {
							writeToNet.writeObject(new Message(TRANSACTION_ABORTED, accountNumber));
						}
						catch(Exception e) {
							System.out.println("[TransactionManagerWorker.run] TRANSACTION_ABORTED - Error when writing the response to client");
						}
					}
				case READ_REQUEST:
					
					// call AccountManager to acquire a READ Lock.
					content = (Object[]) message.getContent();
					accountNumber = (int) content[0];
					transactionId = (int) content[1];
					
					System.out.println("account Number " + accountNumber + "transactionId "+ transactionId);
					
					for(int i=0; i<runningTransactions.size(); i++) {
						if(runningTransactions.get(i).getTransactionId() == transactionId) {
							transaction = runningTransactions.get(i);
							break;
						}
					}
					
					transaction.log("[TransactionManagerWorker.run] " + READ_COLOR + "READ_REQUEST" + RESET_COLOR + ">>>>>>>>> accountNumber being read from" + accountNumber );
					System.out.println(READ_COLOR + " READ_REQUEST " + RESET_COLOR + " #" + transaction.getTransactionId());
					
					success = TransactionServer.lockManager.setLock(transaction, WRITE);
					
					if(success) {
						
						balance = accountManager.read(accountNumber);
					
						try {
							System.out.println("balance is: " + balance);
							writeToNet.writeObject(new Message(READ_REQUEST_RESPONSE, balance));
						}
						catch(Exception e) {
							System.out.println("[TransactionManagerWorker.run] READ_REQUEST_RESPONSE - Error when writing the response to client");
						}
					}
					else
					{
						try {
							writeToNet.writeObject(new Message(TRANSACTION_ABORTED, balance));
						}
						catch(Exception e) {
							System.out.println("[TransactionManagerWorker.run] TRANSACTION_ABORTED - Error when writing the response to client");
						}
					}
					
				case GET_SUM:
					
					int sum = getSum();
					
					try {
						writeToNet.writeObject(new Message(GET_SUM, sum));
					}
					catch(Exception e) {
						System.out.println("[TransactionManagerWorker.run] GET_SUM - Error when writing the response to client");
					}
			}
		}		
	  }
	}
}
