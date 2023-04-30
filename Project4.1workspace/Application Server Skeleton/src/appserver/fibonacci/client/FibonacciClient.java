package appserver.fibonacci.client;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Properties;

import appserver.comm.Message;
import appserver.comm.MessageTypes;
import appserver.job.Job;
import utils.PropertyHandler;

public class FibonacciClient extends Thread implements MessageTypes{

	String host = null;
    int port;

    Properties properties;
    
    Integer num = null;
    
	public FibonacciClient(String serverPropertiesFile, Integer num) {
        try {
            properties = new PropertyHandler(serverPropertiesFile);
            host = properties.getProperty("HOST");
            System.out.println(" [FibonacciClient.FibonacciClient] Host: " + host);
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println(" [FibonacciClient.FibonacciClient] Port: " + port);
            this.num = num;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
	
	public void run() {
		try { 
	        // connect to application server
	        Socket server = new Socket(host, port);
	        
	        // hard-coded string of class, aka tool name ... plus one argument
	        String classString = "appserver.job.impl.Fibonacci";
	        
	        // create job and job request message
	        Job job = new Job(classString, this.num);
	        Message message = new Message(JOB_REQUEST, job);
	        
	        // sending job out to the application server in a message
	        ObjectOutputStream writeToNet = new ObjectOutputStream(server.getOutputStream());
	        writeToNet.writeObject(message);
	        //System.out.println("job was sent");
	        // reading result back in from application server
	        // for simplicity, the result is not encapsulated in a message
	        ObjectInputStream readFromNet = new ObjectInputStream(server.getInputStream());
	        //System.out.println("result was received");
	        Integer result = (Integer) readFromNet.readObject();
	        System.out.println(" Finonacci of " + this.num +": " + result);
	    } catch (Exception ex) {
	        System.err.println(" [FibonacciClient.run] Error occurred ");
	        ex.printStackTrace();
	    }
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		for(int i=46; i>0; i--) {
			(new FibonacciClient("config/Server.properties", i)).start();
		}
	}

}
