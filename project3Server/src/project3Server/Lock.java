package project3Server;

public class Lock 
{


	//constructor 
	public Lock()
	{
	    System.out.println("lock constructor called here");
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

	public void acquire( int transID)
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

	public void release(int transID)
	{
	    System.out.println("release was called for transID " + transID);
	}

}
