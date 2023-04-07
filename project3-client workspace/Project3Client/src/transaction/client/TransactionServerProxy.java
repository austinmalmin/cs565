package transaction.client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import transaction.comm.Message;
import transaction.comm.MessageTypes;
import transaction.server.lock.TransactionAbortedException;

/*
 * Progress Note: 
 * 		Need to create random transactions.
 */
public class TransactionServerProxy implements MessageTypes{
	
	String host = null;
	int port;
	
	private Socket dbConnection = null;
	private ObjectOutputStream writeToNet = null;
	private ObjectInputStream readFromNet = null;
	private Integer transactionID = 0;
	/**
	* Constructor
	* @param host IP address of the transaction server
	* @param port port number of the transaction server
	＊*/
	
	TransactionServerProxy(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public int openTransaction() {
		
		try {
			dbConnection = new Socket(host, port);
			writeToNet = new ObjectOutputStream(dbConnection.getOutputStream()) ;
			readFromNet = new ObjectInputStream(dbConnection. getInputStream()) ;
		} catch (IOException ex) {
			System.out.println(" [TransactionServerProxy.openTransaction] Error occurred when opening object streams"); 
			ex.printStackTrace();
		}
		try {
			writeToNet.writeObject(new Message(OPEN_TRANSACTION, null));
			transactionID = (Integer) readFromNet.readObject();
		} 
		catch (IOException | ClassNotFoundException | NullPointerException ex) {
			System.out.println(" [TransactionServerProxy.openTransaction] Error when writing/reading messages");
			ex.printStackTrace();
		}
		return transactionID;
	}
	
	//implement this method with locking
	public int closeTransaction() {
		
		int  returnStatus = TRANSACTION_COMMITTED;
		
		try {

			writeToNet.writeObject(new Message(CLOSE_TRANSACTION, null));
			returnStatus = (Integer) readFromNet.readObject();
			
			readFromNet.close();
			writeToNet.close();
		}
		catch(Exception ex) {
			System.err.print("Error in closing Transaction");
			ex.printStackTrace();
		}
		return returnStatus;
	}
	
	public int read(int accountNumber) throws TransactionAbortedException
	{
		Message message = new Message(READ_REQUEST, accountNumber);
		try
		{
			writeToNet.writeObject (message);
			message = (Message) readFromNet.readObject();
		} 
		catch (Exception ex) 
		{
			System.out.println(" [TransactionServerProxy. read] Error occurred");
			ex.printStackTrace () ;
		}
		if (message.getType() == READ_REQUEST_RESPONSE) {
			return (Integer) message.getContent();
		}
		else {		
			throw new TransactionAbortedException();
		}
	}
	
	public void write(int accountNumber, int amount) throws TransactionAbortedException
	{
		Object[] content = new Object[] {accountNumber, amount};
		Message message = new Message(WRITE_REQUEST, content);
		
		try {
			writeToNet.writeObject(message);
			message = (Message) readFromNet.readObject();
			
		}
		catch(IOException | ClassNotFoundException ex) {
			System.out.println("[TransactionServerProxy.write] Error occurred: IOException | ClassNotFoundException");
			ex.printStackTrace();
			System.err.print("\n\n");
		}
		if (message.getType() == TRANSACTION_ABORTED)
		{
		// here we have an ABORT TRANSACTION
			throw new TransactionAbortedException();
		}
	}
}