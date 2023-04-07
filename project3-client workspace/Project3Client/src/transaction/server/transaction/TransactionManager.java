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
//Add Colors once we format output

//Progress Note: Need to add code for switch statements

public class TransactionManager extends Thread implements MessageTypes, LockTypes
{	
	
	private static int transactionIdCounter = 0;
	private static final ArrayList<Transaction> runningTransactions = new ArrayList<> ();
	private static final ArrayList<Transaction> abortedTransactions = new ArrayList<>();
	private static final String OPEN_COLOR = null;
	private static final String RESET_COLOR = null;
	private static int transactionNumberCounter = 0;
	
	static AccountManager accountManager = null;
	static LockManager lockManager = null;
	
	private static final ArrayList<Transaction> committedTransactions = new ArrayList<> ();
	
	 public TransactionManager() {
		 //
	 }
	
	public ArrayList<Transaction> getAbortedTransaction()
	{
		return abortedTransactions;
	}
	
	public synchronized void runTransaction(Socket client) 
	{
		(new TransactionManagerWorker(client)).start();
		
	}
	
	public class TransactionManagerWorker extends Thread
	{
		
		private static final String COMMIT_COLOR = null;
		private static final String READ_COLOR = null;
		private static final String WRITE_COLOR = null;
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
						transaction = new Transaction(++transactionIdCounter);
						
						runningTransactions.add(transaction);
					}
					try
					{
						writeToNet.writeObject((Integer)transaction.getTransactionId());
					}
					catch(IOException e)
					{
						System.err.println("[TransactionManagerWorker.run] OPEN_TRANSACTION #" + transaction.getTransactionId() + " - Error writing transactionID");
					}
					
					transaction.log("[TransactionManagerWorker.run] " + OPEN_COLOR + "OPEN_TRANSACTION" + RESET_COLOR + " #" +
							transaction.getTransactionId());
					break;
					
					
				case CLOSE_TRANSACTION:
					
					TransactionServer.lockManager.unlock(transaction);
					runningTransactions.remove(transaction);
					committedTransactions.add(transaction);
					
					try {
						
						writeToNet.writeObject((Integer) TRANSACTION_COMMITTED);
						
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
					
					if(TransactionServer.transactionView) {
						System.out.println(transaction.getLog());
					}
					break;
					
				case WRITE_REQUEST:
				
					 //call AccountManager to acquire a WRITE Lock.
					accountNumber = (Integer) message.getContent();
					transaction.log("[TransactionManagerWorker.run] " + WRITE_COLOR + "WRITE_REQUEST" + RESET_COLOR + ">>>>>>>>> accountNumber being wrote from" + accountNumber );
					TransactionServer.lockManager.setLock(transaction, WRITE);
					
					try {
						writeToNet.writeObject(new Message(WRITE_REQUEST_RESPONSE, accountNumber));
					}
					catch(Exception e) {
						System.out.println("[TransactionManagerWorker.run] CLOSE_TRANSACTION - Error when writing the response to client");
					}
					
				case READ_REQUEST:
					
					// call AccountManager to acquire a READ Lock.
					accountNumber = (Integer) message.getContent();
					transaction.log("[TransactionManagerWorker.run] " + READ_COLOR + "READ_REQUEST" + RESET_COLOR + ">>>>>>>>> accountNumber being read from" + accountNumber );
					TransactionServer.lockManager.setLock(transaction, READ);
					try {
						writeToNet.writeObject(new Message(READ_REQUEST_RESPONSE, accountNumber));
					}
					catch(Exception e) {
						System.out.println("[TransactionManagerWorker.run] CLOSE_TRANSACTION - Error when sending the response to client");
					}
					 
				case TRANSACTION_ABORTED:
					
					//Release all locks according to the transaction.
					
					//add the transaction to the list of aborted transactions
		
			}
		}		
	  }
	}
}
