package appserver.server;

import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.comm.ConnectivityInfo;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Properties;
import utils.PropertyHandler;

/**
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Server {

    // Singleton objects - there is only one of them. For simplicity, this is not enforced though ...
    static SatelliteManager satelliteManager = null;
    static LoadManager loadManager = null;
    static ServerSocket serverSocket = null;

    String host = null;
    int port;
    
    Properties properties;
    
    public Server(String serverPropertiesFile) {

        // create satellite manager and load manager
        // ...
    	loadManager = new LoadManager();
    	satelliteManager = new SatelliteManager();
        // read server properties and create server socket
        // ...
    	try {
            properties = new PropertyHandler(serverPropertiesFile);
            host = properties.getProperty("HOST");
            System.out.println("[PlusOneClient.PlusOneClient] Host: " + host);
            port = Integer.parseInt(properties.getProperty("PORT"));
            System.out.println("[PlusOneClient.PlusOneClient] Port: " + port);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    	
    	try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    }

    public void run() {
    // serve clients in server loop ...
    // when a request comes in, a ServerThread object is spawned
    // ...
    	while(true) {
    		try {
				(new ServerThread(serverSocket.accept())).start();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }

    // objects of this helper class communicate with satellites or clients
    private class ServerThread extends Thread {

        Socket client = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        private ServerThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            // set up object streams and read message
            // ...
        	try {
				readFromNet = new ObjectInputStream(client.getInputStream());
				writeToNet = new ObjectOutputStream(client.getOutputStream());
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
            
            // process message
        	ConnectivityInfo conInfo = null;
        	
        	try {
				message = (Message) readFromNet.readObject();
				System.out.println("message received");

			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
        	
            switch (message.getType()) {
                case REGISTER_SATELLITE:
                    // read satellite info
                    // ...
                    conInfo = (ConnectivityInfo) message.getContent();
                    // register satellite
                    synchronized (Server.satelliteManager) {
                        // ...
                    	satelliteManager.registerSatellite(conInfo);
                    }
        			System.out.println("satellite registered!");

                    // add satellite to loadManager
                    synchronized (Server.loadManager) {
                        // ...
                    	loadManager.satelliteAdded(conInfo.getName());
                    }
                    
        			System.out.println("satellite added to load manager");

                    break;

                case JOB_REQUEST:
                    System.err.println("\n[ServerThread.run] Received job request");

                    String satelliteName = null;
                    synchronized (Server.loadManager) {
                        // get next satellite from load manager
                        // ...
                    	// get connectivity info for next satellite from satellite manager
                        // ...
                    	try {
							satelliteName = loadManager.nextSatellite();
							conInfo = satelliteManager.getSatelliteForName(satelliteName);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
                        
                    	
                    }

                    Socket satellite = null;
                    // connect to satellite
                    // ...
					try {
						satellite = new Socket(conInfo.getHost(), conInfo.getPort());
						
						// open object streams,
						ObjectInputStream satelliteReadFromNet = new ObjectInputStream(satellite.getInputStream());
						ObjectOutputStream satelliteWriteToNet = new ObjectOutputStream(satellite.getOutputStream());
						
						// forward message (as is) to satellite,
						satelliteWriteToNet.writeObject(message);
						
						// receive result from satellite and
						Integer result = (Integer) satelliteReadFromNet.readObject();
						
						// write result back to client
	                    // ...
						writeToNet.writeObject(result);
						
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    // open object streams,
					catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                    
                    break;

                default:
                    System.err.println("[ServerThread.run] Warning: Message type not implemented");
            }
        }
    }

    // main()
    public static void main(String[] args) {
        // start the application server
        Server server = null;
        if(args.length == 1) {
            server = new Server(args[0]);
        } else {
            server = new Server("config/Server.properties");
        }
        server.run();
    }
}
