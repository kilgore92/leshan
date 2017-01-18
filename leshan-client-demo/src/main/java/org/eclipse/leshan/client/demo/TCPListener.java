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
		            	// TODO remove these 2 lines and call methods on MyLight
		                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
		                out.println(new Date().toString());
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
				tcpServer = new Thread(this);
		}
		else {
			LOG.info("Error! The server was started already...");
		}
		
	}
}
