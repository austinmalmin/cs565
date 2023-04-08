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
 * 		completed
 */
public class TransactionServerProxy implements MessageTypes{
	
	String host = null;
	int port;
	
	private Socket dbConnection = null;
	private ObjectOutputStream writeToNet = null;
	private ObjectInputStream readFromNet = null;
	private Integer transactionID = 0;
	Object[] content;
	/**
	* Constructor
	* @param host IP address of the transaction server
	* @param port port number of the transaction server
	＊*/
	
	TransactionServerProxy(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	/*
	 * opens the transaction 
	 * returns transactionId
	 */
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
			Message message = (Message) readFromNet.readObject();
			transactionID = (Integer) message.getContent();
		} 
		catch (IOException | ClassNotFoundException | NullPointerException ex) {
			System.out.println(" [TransactionServerProxy.openTransaction] Error when writing/reading messages");
			ex.printStackTrace();
		}
		return transactionID;
	}
	
	/*
	 * closes transacttion from server
	 */
	public int closeTransaction() {
		
		int  returnStatus = TRANSACTION_COMMITTED;
		
		try {

			writeToNet.writeObject(new Message(CLOSE_TRANSACTION, null));
			//returnStatus = (Integer) readFromNet.readObject();
			
			readFromNet.close();
			writeToNet.close();
		}
		catch(Exception ex) {
			System.err.print("Error in closing Transaction");
			ex.printStackTrace();
		}
		return returnStatus;
	}
	
	/*
	 * gets account balance from server
	 */
	public int read(int accountNumber, int transactionId) throws TransactionAbortedException
	{
		content = new Object[] {accountNumber, transactionId};
		
		Message message = new Message(READ_REQUEST, content);
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
	
	/*
	 * write amount to account number on server
	 */
	public void write(int accountNumber, int amount, int transactionId) throws TransactionAbortedException
	{
		Object[] content = new Object[] {accountNumber, amount, transactionId};
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
	
	public int getSum() 
	{
		try {
			dbConnection = new Socket(host, port);
			writeToNet = new ObjectOutputStream(dbConnection.getOutputStream()) ;
			readFromNet = new ObjectInputStream(dbConnection. getInputStream()) ;
		} catch (IOException ex) {
			System.out.println(" [TransactionServerProxy.openTransaction] Error occurred when opening object streams"); 
			ex.printStackTrace();
		}
		
		try {
			writeToNet.writeObject(new Message(GET_SUM, null));
			Message message = (Message) readFromNet.readObject();
			return (int) message.getContent();
		}
		catch(IOException | ClassNotFoundException ex) {
			System.out.println("[TransactionServerProxy.write] Error occurred: IOException | ClassNotFoundException");
			ex.printStackTrace();
			System.err.print("\n\n");
		}
		return 0;
	}
}