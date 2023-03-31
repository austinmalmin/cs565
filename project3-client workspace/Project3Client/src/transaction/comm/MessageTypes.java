package transaction.comm;

/**
 * Interface [MessageTypes] Defines the different message types used in the application.
 * Any entity using objects of class Message needs to implement this interface.
 * 
 *
 */
public interface MessageTypes {
    
	public static final int OPEN_TRANSACTION =       0;
	public static final int CLOSE_TRANSACTION =      1;
	public static final int WRITE_REQUEST =          2;
	public static final int READ_REQUEST =           3;
	public static final int READ_REQUEST_RESPONSE =  4;
	public static final int TRANSACTION_ABORTED =    5;  
}