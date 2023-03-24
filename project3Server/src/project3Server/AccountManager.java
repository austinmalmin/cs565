package project3Server;

public class AccountManager 
{


	//constructor
	public AccountManager()
	{

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

	public void read()
	{
	    System.out.println("read was called\n");

	    //checks if locking is active

	        //attempts to get read lock via lockManager

	            //success: read account values

	        //otherwise deadLock detected, abort transaction

	        //log action

	    //read account value 

	    //return data

	            
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

	public int write(int value, int accountID)
	{
	    //init flag to failure
	    int successFlag = -1;
	    System.out.println("write was called");

	    //check if locking is active

	        //attempt to acquire a write lock on the account 

	            //success-> update account value to value 

	            //set return flag to success

	        //otherwise deadlock detected and thread aborted
	        
	        //set return flag to aborted

	        //log action


	    //otherwise locking is inactive
	        
	        //update account value to value 

	        //set return flag to success

	    //return flag 
	    return successFlag;

	}
}


