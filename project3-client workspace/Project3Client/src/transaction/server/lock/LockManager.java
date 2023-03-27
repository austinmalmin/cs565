package transaction.server.lock;

import transaction.server.account.Account;
import transaction.server.transaction.Transaction;

public class LockManager {
	
	public void setLock(Account account, Transaction transactionId, LockTypes lockType ) {
		/* Find the lock
		 * acquire the lock of type lockType
		 */
	}
	
	public synchronized void unlock(Transaction transactionId) {
		/*Iterate through all locks on Account
		 * Release the lock
		 */
	}
}


