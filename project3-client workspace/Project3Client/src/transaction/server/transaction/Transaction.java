package transaction.server.transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.Lock;

import transaction.server.TransactionServer;

//Complete - bug on getLocks()
public class Transaction {

	int transactionId;
	ArrayList<Lock> locks = null;
	HashMap<Integer, Integer> beforeImage ;
	
	StringBuffer log = new StringBuffer("");
	
	
	public Transaction(int transactionId) {
		// TODO Auto-generated constructor stub
		this.transactionId = transactionId;
		this.locks = new ArrayList();
		this.beforeImage = new HashMap();
	}


	/**
	 * @return the transactionId
	 */
	public int getTransactionId() {
		return transactionId;
	}

	/**
	 * @return the locks
	 */
	public ArrayList<Lock> getLocks() {
		return locks;
	}


	/**
	 * @param locks the locks to set
	 */
	public void addLock(Lock lock) {
		locks.add(lock);
	}


	/**
	 * @return the beforeImage
	 */
	public HashMap<Integer, Integer> getBeforeImage() {
		return beforeImage;
	}


	/**
	 * @param beforeImage the beforeImage to set
	 */
	public void addBeforeImage(int  account, int balance) {
		beforeImage.put(account, balance);
		this.log("[Transaction.addBeforeImage] | set Before Image for account" + account + "and balance"+ balance);
	}

	
	public void log(String logString) {
		int messageCount = TransactionServer.getMessageCount();
		log.append("\n").append(messageCount).append(" ").append(logString);
		
		if(!TransactionServer.transactionView)
		{
			System.out.println(messageCount + "Transaction #" +transactionId);
		}
	}
	/**
	 * @return the log
	 */
	public StringBuffer getLog() {
		return log;
	}



}
