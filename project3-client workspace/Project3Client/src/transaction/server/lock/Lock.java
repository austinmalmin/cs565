package transaction.server.lock;


import java.util.ArrayList;
import java.util. HashMap;
import java.util. Iterator;
import transaction.server.transaction. Transaction;
import transaction.server.account. Account;

public class Lock implements LockTypes
{
	private int currentLockType;
	private final Account account;
	private final ArrayList<Transaction> lockHolders;
	private final HashMap<Transaction, Object[]> lockRequestors;

	private static String preFixLogString = "[Lock.acquire]   |";
	/**
		•	  Constructor
		•	  @param account**/
	
	public Lock(Account account)
	{
		this.account = account;
		this.lockHolders = new ArrayList();
		this.lockRequestors = new HashMap();
		this.currentLockType = EMPTY_LOCK;

	}
	


	/*

	Function: acquire

	Process: acquires a lock for a certain account # if there is a conflicting lock,
	        then the thread will check for deadlocks then sleep 

	Input data (parameter): transactionID

	Output data(parameter): 

	Output data (return):

	Dependencies: release


	*/

	public void acquire( int transID, LockTypes lockType)
	{
	    System.out.println("acquire lock was called for transID " + transID);
	}

	/*

	Function: release

	Process: releases all locks a transactionID holds then notifies all 
	        sleeping threads 

	Input data (parameter): transactionID

	Output data(parameter): 

	Output data (return):

	Dependencies: 


	*/

	public void release()
	{
	    System.out.println("release was called");
	}

}
