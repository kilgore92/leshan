package org.eclipse.leshan.client.demo;
import java.io.*;                                                                                                                                                                         
import java.net.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory; 

public class TCPClient {
	
	private static final Logger LOG = LoggerFactory.getLogger(TCPClient.class);
	
	public TCPClient() {
		LOG.info("Instanced TCP Client \n");		
	}
	
	public boolean send(String command){
		try {
			Socket clientSocket = new Socket("localhost", 9090);
			LOG.info("Established Connection with SenseHat Process\n");
			DataOutputStream outToServer;
			outToServer = new DataOutputStream(clientSocket.getOutputStream());
			outToServer.writeBytes(command);                                                                                                                                                
			clientSocket.close(); 
			return true;
		} catch (IOException e) {
			// TODO Potential bug, socket not closing. but YOLO.
			e.printStackTrace();
			return false;
		}    
	}
}
