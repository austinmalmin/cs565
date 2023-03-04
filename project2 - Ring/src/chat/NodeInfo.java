package chat;

import java.io.Serializable;

import message.MessageTypes;

/*
 * PROGRESS NOTES:
 * this class is complete
 */
public class NodeInfo implements Serializable{
	//init variables
	int port;	
	String IpAddress;	
	String name;
	
	//Constructor
	public NodeInfo(String IpAddress,int port , String name) {
		this.port = port;
		this.IpAddress = IpAddress;
		this.name = name;
	}
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
	
	public String getName() {
		return name;
	}
	
	public boolean equals( Object o) {
		if( !(o instanceof NodeInfo)) {
			System.out.print("returnd false\n");
			return false;
		}
		NodeInfo other = (NodeInfo) o;
		return port == other.port && IpAddress.equals(other.IpAddress); 
	}
}
