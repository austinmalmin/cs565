package transaction.client;


import java.io.Serializable;

public class NodeInfo implements Serializable{
	//init variables
	int port;	
	String IpAddress;
	int startingBal;
	int numAccounts;
	
	//Constructor 
	public NodeInfo(String IpAddress,int port) {
		this.port = port;
		this.IpAddress = IpAddress;
	}
	
	//getors
	public int getPort() {
		return port;
	}
	
	public String getIp() {
		return IpAddress;
	}
	
	
	public int getStartingBal() {
		return startingBal;
	}
	public int getNumAccounts() {
		return numAccounts;
	}
	

}