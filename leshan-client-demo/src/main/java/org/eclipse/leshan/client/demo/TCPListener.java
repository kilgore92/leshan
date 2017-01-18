package org.eclipse.leshan.client.demo;

import java.io.PrintWriter;
import java.net.*;
import java.util.Date;

public class TCPListener implements Runnable {
	private Thread tcpServer;
	@SuppressWarnings("unused")
	private MyLight controllingLight; // Need this object reference to call it's methods
	public TCPListener(MyLight myLight) {
		this.controllingLight = myLight;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
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
				System.out.println("Error here");
			}
			
		    finally {
		        listener.close();
		    }
		} 
			catch (Exception e) {
			// TODO: handle exception
			System.out.println("Error here!!!!");
		}
		
	}
	
	public String getServerStatus() {
		return (tcpServer.getState().toString());
		
	}
	
	public void start() {
		System.out.println("Trying to start the TCP server...");
		if (tcpServer == null) {
				tcpServer = new Thread(this);
		}
		else {
			System.out.println("Error! The server was started already...");
		}
		
	}
}
