package transaction.server.lock;


import java.util.ArrayList;
import java.util. HashMap;
import java.util. Iterator;
import java.util.List;

import transaction.server.transaction.Transaction;
import transaction.server.account.Account;

/*
 * completed
 */
public class Lock implements LockTypes
{
	public int currentLockType;
	public final Account account;
	public final ArrayList<Transaction> lockHolders;
	public final HashMap<Transaction, Integer[]> lockRequestors;

	private static String preFixLogString = "[Lock.acquire]   |";
	/**
		•	  Constructor
		•	  @param account**/
	
	public Lock(Account account)
	{
		this.account = account;
		this.lockHolders = new ArrayList<Transaction>();
		this.lockRequestors = new HashMap<Transaction, Integer[]>();
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
		//once you passed the conflict loop you recieve a lock.
		addLockHolder(transaction, lockType);
	}
	
	
	private void addLockHolder(Transaction transaction, int lockType) {
		lockHolders.add(transaction);
		Lock newLock = new Lock(account);
		newLock.currentLockType = lockType;
		transaction.addLock(newLock);
	}
	
	private void removeLockRequestor(Transaction transaction) {
		// TODO Auto-generated method stub
		lockRequestors.remove(transaction);
	}



	public void addLockRequestor(Transaction transaction, int lockType) {
		// TODO Auto-generated method stub
		//Not correct - fix it 
		Integer[] lockarrayObj =  {lockType, transaction.getTransactionId()};
		lockRequestors.put(transaction, lockarrayObj);
		
		
	}



	private boolean isConflict(Transaction transaction, int lockType) {
		// TODO Auto-generated method stub
		ArrayList<Lock> tLocks = transaction.getLocks();
		
		if(tLocks.isEmpty()) {
			return false;
		}
		else {
			Iterator<Lock> lockIterator = tLocks.iterator();
			
			while(lockIterator.hasNext()) {
				
				Lock currentLock = (Lock) lockIterator.next();
				
				if(!(currentLock.getLockType(currentLock) == READ && lockType == READ)) {
					return true; //conflict
				}
			}
			return false;
		}
			
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
		transaction.log(preFixLogString + " releasing all locks " + " on account #" + account.getId());
		
			ArrayList<Lock> locks = transaction.getLocks();
			Iterator<Lock> lockIterator = locks.iterator();
			Lock checkedLock;
			
			while(lockIterator.hasNext()) {
				checkedLock = lockIterator.next();
				checkedLock.currentLockType = EMPTY_LOCK;
				locks.remove(checkedLock);
			}
		notifyAll();
	}
}