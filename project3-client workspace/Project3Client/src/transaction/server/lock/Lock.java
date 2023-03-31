package transaction.server.lock;


import java.util.ArrayList;
import java.util. HashMap;
import java.util. Iterator;
import transaction.server.transaction.Transaction;
import transaction.server.account.Account;

//Largely done - fix the error in the hashmap!
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

	public void acquire( Transaction transaction, int lockType) throws TransactionAbortedException
	{
	    //System.out.println("acquire lock was called for transID " + transID);
		transaction.log(preFixLogString + " trying to set " + getLockTypeString(lockType)  + " on account #" + account.getId());
		
		//starting the conflict loop
		while(isConflict(transaction, lockType)) {
			
			
			ArrayList<Lock> locks = transaction.getLocks();
			Iterator<Lock> lockIterator = locks.iterator();
			Lock checkedLock;
			
			while(lockIterator.hasNext()) {
				checkedLock = lockIterator.next();
				
				if(!checkedLock.lockRequestors.isEmpty()) {
					transaction.log(preFixLogString + "Aborting when trying to set lock " + getLockTypeString(lockType) + 
							"on account number #" + account.getId() + " while holding a " + getLockTypeString(checkedLock.currentLockType) + 
							" on account #" + checkedLock.account.getId());
					
					throw new TransactionAbortedException();
				}
			}
			
			transaction.log(preFixLogString + " ----> waiting to set " + getLockTypeString(lockType) + "on account #" + account.getId());
			
			addLockRequestor(transaction, lockType);
			
			try {
				wait();
			}
			catch(InterruptedException e) {
				
			}
			
			removeLockRequestor(transaction);
			
			transaction.log(preFixLogString + " <---- woke up again trying to set " + getLockTypeString(lockType) + "on account #" + account.getId());
 		}
	}

	private void removeLockRequestor(Transaction transaction) {
		// TODO Auto-generated method stub
		lockRequestors.remove(transaction);
	}



	public void addLockRequestor(Transaction transaction, int lockType) {
		// TODO Auto-generated method stub
		//Not correct - fix it 
		Object[] lockarrayObj = new Object[lockType];
		lockRequestors.put(transaction, lockarrayObj);
	}



	private boolean isConflict(Transaction transaction, int lockType) {
		// TODO Auto-generated method stub
		return false;
	}



	public String getLockTypeString(int lockType) {
		// TODO Auto-generated method stub
		switch(lockType) {
			case EMPTY_LOCK:
				return "EMPTY_LOCK";
			case READ:
				return "READ";
			case WRITE:
				return "WRITE";
		}
		return null;
	}

	
	public int getLockType(Lock lock) {
		
		return lock.currentLockType;
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

	public void release(Transaction transaction)
	{
	    //System.out.println("release was called");
		transaction.log(preFixLogString + " releasing all locks " + " on account #" + account.getId());
			
			//fix the getlokcs
			ArrayList<Lock> locks = transaction.getLocks();
			Iterator<Lock> lockIterator = locks.iterator();
			Lock checkedLock;
			
			while(lockIterator.hasNext()) {
				checkedLock = lockIterator.next();
				locks.remove(checkedLock);
			}
	

	}
}