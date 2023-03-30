package transaction.server.account;

import transaction.server.TransactionServer;

import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class AccountManager extends Thread
{

	
	ArrayList<Account> accountList = new ArrayList<>();
	
	StringBuffer log = new StringBuffer("");
	
	 int transactionID;
	 int transactionNumber;
	  
	//constructor todo
	public AccountManager(int numAccounts, int startingBal) {
		// TODO Auto-generated constructor stub
		int i=0;
		while(i<numAccounts)
		{
			//creates i accounts with balance startingBal.
			
			accountList.add(new Account(i, startingBal));
			i++;
		}
	}


	/*
	
	Function: read

	Process: attempts to acquire a read lock on an account if locking is active
	        upon success, reads the account values

	Input data (parameter): 

	Output data(parameter): 

	Output data (return): account info

	Dependencies: 
	 */
	public int read (int accountNumber)
	{
		

		// check if value to be read was written by this transaction
		/*checks if locking is active
		 * 
		 * //attempts to get read lock via lockManager
		 * 
		 * //success: read account values
		 * 
		 * //otherwise deadLock detected, abort transaction
		 * 
		 * //log action
		 * 
		 * //read account value
		 * 
		 * //return data*/
		return -1;
	}
	
	/*

	Function: write

	Process: seeks to acquire a write lock on an account
	        this can be a promoted read lock or estb a new write lock
	        once lock has been acquired, write a value to an account 

	Input data (parameter): value, accountID

	Output data(parameter): 

	Output data (return): int value indicating success

	Dependencies: 


	*/
	public int write(int accountNumber, int newBalance) {
		//check if locking is active
		/* 
		 * //attempt to acquire a write lock on the account
		 * 
		 * //success-> update account value to value
		 * 
		 * //set return flag to success
		 * 
		 * //otherwise deadlock detected and thread aborted
		 * 
		 * //set return flag to aborted
		 * 
		 * //log action
		 * 
		 * 
		 * //otherwise locking is inactive
		 * 
		 * //update account value to value
		 * 
		 * //set return flag to success
		 * 
		 * //return flag return successFlag;*/
		return -1;
	}
	

	public int getTransactionID() {
		return transactionID;
	}


	public void setTransactionID(int transactionID) {
		this.transactionID = transactionID;
	}


	public int getTransactionNumber() {
		return transactionNumber;
	}


	public void setTransactionNumber(int transactionNumber) {
		this.transactionNumber = transactionNumber;
	}


	public void log(String string) {
		// TODO Auto-generated method stub
		
	}
	
}



