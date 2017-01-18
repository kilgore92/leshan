package org.eclipse.leshan.client.demo;

import java.io.PrintWriter;
import java.net.*;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TCPListener implements Runnable {
	private static final Logger LOG = LoggerFactory.getLogger(TCPListener.class);
	private Thread tcpServer;
	@SuppressWarnings("unused")
	private MyLight controllingLight; // Need this object reference to call it's methods
	public TCPListener(MyLight myLight) {
		LOG.info("Creating listener class");
		this.controllingLight = myLight;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		LOG.info("Starting to listen...");
		try {
			ServerSocket listener = new ServerSocket(9091);
			try {
		        while (true) {
		            Socket socket = listener.accept();
		            try {
		            	// TODO : Accept current status from MQTT client script
		            	// Access the myLight object and
		            	// 1. Get the latest R,G,B via the getter methods (if FREE->USED)
		            	// 2. If USED->FREE init R,G,B with "low light" values
		            	// 3. Call the setRGB function with the RGB values
		            	
		            	// TODO remove these 2 lines and call methods on MyLight
		                //PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		                //out.println(new Date().toString());
		            } finally {
		                socket.close();
		            }
		        }
		    }
			catch (Exception e){
				LOG.error("Error here");
			}
			
		    finally {
		        listener.close();
		    }
		} 
			catch (Exception e) {
			// TODO: handle exception
				LOG.error("Error here!!!!");
		}
		
	}
	
	public String getServerStatus() {
		return (tcpServer.getState().toString());
		
	}
	
	public void start() {
		LOG.info("Trying to start the TCP server...");
		if (tcpServer == null) {
				LOG.info("Starting TCP Server for MQTT communication");
				tcpServer = new Thread(this);
				tcpServer.start();
		}
		else {
			LOG.info("Error! The server was started already...");
		}
		
	}
}
