package com.frick.lmac.main;

import java.util.Enumeration;
import javax.comm.CommPortIdentifier;

/*******************************************************************
 * CLASS CommPortFinder 
 * AUTHOR : Lachlan R McCallum
 * This class gathers a last of ports specified by the javax.comm driver 
 * and instantiates a virtual identified port which can be utilized to 
 * connect to hardware IO
 ********************************************************************/

public class CommPortFinder {

	private static CommPortIdentifier portID;
	//List of available ports
	private static Enumeration portList;
	private boolean portFound = false;
	private String defaultPort;
	

	public CommPortFinder(String defaultPort) {
		this.defaultPort = defaultPort;
	}
	
	public CommPortIdentifier getPort() {
		
		//Identifies ports specified by javax-comm property file
		portList = (CommPortIdentifier.getPortIdentifiers());
				
		//Iterates through available ports
		while(portList.hasMoreElements()) {
			portID = (CommPortIdentifier) portList.nextElement();
			//ensures that the port is a SERIAL port
			if(portID.getPortType() == CommPortIdentifier.PORT_SERIAL) {
				//locates the default port for communication
				if(portID.getName().equals(defaultPort)) {
					Feedback.log("Port: " + defaultPort  + " initilized successfully");
					portFound = true;

					return portID;

				}
					
			}
		}
		portFound = false;
		return null;
	}
	
	public CommPortIdentifier getPortID() {
		return portID;
	}
	
}
