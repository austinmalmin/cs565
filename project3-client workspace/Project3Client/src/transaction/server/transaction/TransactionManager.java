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

//Add Colors once we format output

/*import static utils.TerminalColors.ABORT_COLOR;
import static utils.TerminalColors.OPEN_COLOR;
import static utils.TerminalColors.READ_COLOR;
import static utils.TerminalColors.RESET_COLOR;
import static utils.TerminalColors.WRITE_COLOR;*/

public class TransactionManager implements MessageTypes
{	
	
	private static int transactionIdCounter = 0;
	private static final ArrayList<AccountManager> runningTransactions = new ArrayList<> ();
	private static final ArrayList<AccountManager> abortedTransactions = new ArrayList<>();
	private static final String OPEN_COLOR = null;
	private static final String RESET_COLOR = null;
	private static int transactionNumberCounter = 0;
	
	
	 public TransactionManager(Socket client) {
	  
		 
	 }
	
	public ArrayList<AccountManager> getAbortedTransaction()
	{
		return abortedTransactions;
	}
	
	public synchronized void runTransaction(Socket client) 
	{
		(new TransactionManagerWorker(client)).start();
		
	}
	
	public class TransactionManagerWorker extends Thread
	{
		
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
				System.exit (1);
			}
			
			AccountManager transaction = null;
			switch (message.getType())
			{

				case OPEN_TRANSACTION:
					
					synchronized (runningTransactions)
					{
						// assign a new transaction ID, also pass in the last assigned transaction number
						// as to the latter, that number may refer to a (prior, non-overlapping) transaction that needed to be aborted
						transaction = new AccountManager(++transactionIdCounter, transactionNumberCounter);
						runningTransactions.add(transaction);
					}
					try
					{
						writeToNet.writeObject(transaction.getTransactionID());
					}
					catch(IOException e)
					{
						System.err.println("[TransactionManagerWorker.run] OPEN_TRANSACTION #" + transaction.getTransactionID() + " - Error writing transactionID");
					}
					
					transaction.log("[TransactionManagerWorker.run] " + OPEN_COLOR + "OPEN_TRANSACTION" + RESET_COLOR + " #" +
							transaction.getTransactionID());
					break;
					
					
				case CLOSE_TRANSACTION:
					
					synchronized(runningTransactions)
					{
						runningTransactions.remove(transaction);
						// code it here
					}
					
					
				case WRITE_REQUEST:
				
					 //call AccountManager to acquire a WRITE Lock.
				
				case READ_REQUEST:
					
					// call AccountManager to acquire a READ Lock.
					
				case READ_REQUEST_RESPONSE:
					
					//if Lock Acquired
					 	
						// READ Account Balance
					
					//else
					
					  	//Abort The Transaction
					 
				case TRANSACTION_ABORTED:
					
					//Release all locks according to the transaction.
					
					//add the transaction to the list of aborted transactions
		
			}
		}		
	  }
	}
}
