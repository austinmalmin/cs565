package transaction.server.lock;

import java.util.ArrayList;
import java.util.Iterator;

import transaction.server.account.Account;
import transaction.server.transaction.Transaction;

/*
 * Completed
 */
public class LockManager extends Thread{
	
	public LockManager(){
		//to do LM
	}
	public boolean setLock(Transaction transactionId, int lockType ) {
		/* Find the lock
		 * acquire the lock of type lockType
		 */
		ArrayList<Lock> locks = transactionId.getLocks();
		
		Iterator lockIterator = locks.iterator();
		
		while(lockIterator.hasNext()) {
			
			Lock lock = (Lock) lockIterator.next();
			
			if(!lock.lockHolders.contains(transactionId)) {
			
				try {
					
					lock.acquire(transactionId, lockType);
					
				} catch (TransactionAbortedException e) {
					// TODO Auto-generated catch block
					return false;
				}
			}
		}
		return true;
		
	}
	
	public synchronized void unlock(Transaction transactionId) {
		/*Iterate through all locks on Account
		 * Release the lock
		 */
		ArrayList<Lock> lock =  transactionId.getLocks();
		
		Iterator lockIterator = lock.iterator();
		
		while(lockIterator.hasNext()) {
			
			Lock locks = (Lock) lockIterator.next();
			
			if(!locks.lockHolders.contains(transactionId)) {
					
					locks.release(transactionId);
			}
		}
	}
}


