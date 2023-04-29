package appserver.server;

import java.util.ArrayList;

/**
 *
 * @author Dr.-Ing. Wolf-Dieter Otte
 */
public class LoadManager {

    static ArrayList<String> satellites = null;
    static int lastSatelliteIndex = -1;

    public LoadManager() {
        satellites = new ArrayList<String>();
    }

    public void satelliteAdded(String satelliteName) {
        // add satellite
        // ...
    	
    	satellites.add(satelliteName);
    	 
    	for(String item : satellites) {
    		System.out.print("item, " + item);
    	}
    	
    }


    public String nextSatellite() throws Exception {
        
        int numberSatellites;
        
        synchronized (satellites) {
            // implement policy that returns the satellite name according to a round robin methodology
            // ...
        	numberSatellites = satellites.size() - 1;
        	if(lastSatelliteIndex == numberSatellites) {
        		lastSatelliteIndex = 0;
        	}
        	else {
        		lastSatelliteIndex++;
        	}
        }
        
        // ... name of satellite who is supposed to take job
        return (String) satellites.get(lastSatelliteIndex);
        
    }
}
