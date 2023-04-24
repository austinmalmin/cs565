package appserver.satellite;

import appserver.job.Job;
import appserver.comm.ConnectivityInfo;
import appserver.job.UnknownToolException;
import appserver.comm.Message;
import static appserver.comm.MessageTypes.JOB_REQUEST;
import static appserver.comm.MessageTypes.REGISTER_SATELLITE;
import appserver.job.Tool;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import utils.PropertyHandler;
import utils.NetworkUtilities;

/**
 * Class [Satellite] Instances of this class represent computing nodes that execute jobs by
 * calling the callback method of tool a implementation, loading the tool's code dynamically over a network
 * or locally from the cache, if a tool got executed before.
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class Satellite extends Thread {

    private ConnectivityInfo satelliteInfo = new ConnectivityInfo();
    private ConnectivityInfo serverInfo = new ConnectivityInfo();
    private HTTPClassLoader classLoader = new HTTPClassLoader();
    private Hashtable toolsCache = null;
    //cache 
    private HashMap<String, Tool> cache = new HashMap<>();
    //socket
    ServerSocket serverSocket = null;
    
    public Satellite(String satellitePropertiesFile, String classLoaderPropertiesFile, String serverPropertiesFile) {

        // read this satellite's properties and populate satelliteInfo object,
    	
    	Properties satelliteProperties = null;
    	Properties classLoaderProperties = null;
    	Properties serverProperties = null;
    	
    	try {
    		satelliteProperties = new PropertyHandler(satellitePropertiesFile);
    		classLoaderProperties = new PropertyHandler(classLoaderPropertiesFile);
    		serverProperties = new PropertyHandler(serverPropertiesFile);
    	}
    	catch(IOException e) {
    		Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, 
					"error accessing file", e );
			System.exit(1);
    	}
    	
    	// read properties of the application server and populate serverInfo object
        // other than satellites, the as doesn't have a human-readable name, so leave it out
        // ...
    	
    	try {
			serverInfo.setPort(Integer.parseInt(serverProperties.getProperty("PORT")));
			serverInfo.setName(serverProperties.getProperty("NAME"));
		}
		catch (NumberFormatException ex) {
			Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, 
					"error setting server properties", ex );
			System.exit(1);
		}
    	
    	try {
			satelliteInfo.setPort(Integer.parseInt(serverProperties.getProperty("PORT")));
			satelliteInfo.setName(serverProperties.getProperty("NAME"));
		}
		catch (NumberFormatException ex) {
			Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, 
					"error setting satellite properties", ex );
			System.exit(1);
		}
        // which later on will be sent to the server
        // ...

    	
        // read properties of the code server and create class loader
        // -------------------
        // ...

    	try {
    		classLoader.port = (Integer.parseInt(serverProperties.getProperty("PORT")));
    		classLoader.host = (serverProperties.getProperty("HOST"));
		}
		catch (NumberFormatException ex) {
			Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, 
					"error setting class loader properties", ex );
			System.exit(1);
		}
    	
        // create tools cache
        // -------------------
        // ...
        
    }

    @Override
    public void run() {

        // register this satellite with the SatelliteManager on the server
        // ---------------------------------------------------------------
        // ...
        
        
        // create server socket
        // ---------------------------------------------------------------
        // ...
    	try {
			serverSocket = new ServerSocket(serverInfo.getPort());
		} catch (IOException e) {
			Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, 
					"error creating server socket ", e);
			System.exit(1);
		}
    	
        
        // start taking job requests in a server loop
        // ---------------------------------------------------------------
        // ...
    	while(true) {
    		try {
				Socket clientConn = (serverSocket.accept());
				(new SatelliteThread(clientConn, this)).run();
			} catch (IOException e) {
				Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, 
						"error receiving client connection ", e);
				System.exit(1);
			}
    	}
    }

    // inner helper class that is instanciated in above server loop and processes single job requests
    private class SatelliteThread extends Thread {

        Satellite satellite = null;
        Socket jobRequest = null;
        ObjectInputStream readFromNet = null;
        ObjectOutputStream writeToNet = null;
        Message message = null;

        SatelliteThread(Socket jobRequest, Satellite satellite) {
            this.jobRequest = jobRequest;
            this.satellite = satellite;
        }

        @Override
        public void run() {
            // setting up object streams
            // ...
            try {
            	
				writeToNet = new ObjectOutputStream(jobRequest.getOutputStream());
				readFromNet = new ObjectInputStream(jobRequest.getInputStream());
				
				// reading message
	            // ...
				message = (Message) readFromNet.readObject();
				
			} catch (IOException | ClassNotFoundException e) {
				Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, 
						"error creating reader and writer ", e);
				System.exit(1);
			}
            
            
           
            
            switch (message.getType()) {
                case JOB_REQUEST:
                    // processing job request
                    // ...
                    Job job = (Job) message.getContent();
				try {
					Tool tool =  getToolObject(job.getToolName(), satellite);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownToolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                    break;

                default:
                    System.err.println("[SatelliteThread.run] Warning: Message type not implemented");
            }
        }
    }

    /**
     * Aux method to get a tool object, given the fully qualified class string
     * If the tool has been used before, it is returned immediately out of the cache,
     * otherwise it is loaded dynamically
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     * @throws IllegalArgumentException 
     */
    public Tool getToolObject(String toolClassString, Satellite satellite) throws UnknownToolException, ClassNotFoundException, InstantiationException, IllegalAccessException, IllegalArgumentException, NoSuchMethodException, SecurityException {

        Tool toolObject = null;

        // ...check if in cache
        if((toolObject  = satellite.cache.get(toolClassString)) == null) {
        	
        	System.out.println("tool class string " + toolClassString);
        	Class<?> toolObjectClass = satellite.classLoader.loadClass(toolClassString);
        	
        	try {
        		toolObject = (Tool) toolObjectClass.getDeclaredConstructor().newInstance();
        		
        	}
        	catch(InvocationTargetException ex) {
        		Logger.getLogger(Satellite.class.getName()).log(Level.SEVERE, 
						"getToolObject cause InvocationTargetException ", ex);
				System.exit(1);
        	}
        	
        	satellite.cache.put(toolClassString, toolObject);
        }
        else {
        	System.out.println("Tool: " + toolClassString + " already in cache ");
        }
        return toolObject;
    }

    public static void main(String[] args) {
        // start the satellite
    	Satellite satellite = null;
    	
    	if(args.length != 3) {
    		 satellite = new Satellite("config/Satellite.Earth.properties", "config/WebServer.properties", "config/Server.properties");
    	}
    	else {
    		 satellite = new Satellite(args[0], args[1], args[2]);
    	}
    	satellite.run();
    }
}
