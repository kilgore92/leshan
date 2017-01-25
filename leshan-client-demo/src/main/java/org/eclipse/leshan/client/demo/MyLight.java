package org.eclipse.leshan.client.demo;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.californium.scandium.util.Base64.InputStream;
import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.json.JSONArray;
import org.json.JSONObject;



public class MyLight extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(MyLight.class);
    
    private int groupID = -1; // Initialized by the broker during identity binding
    private String roomID = "0"; // Initialized by the broker during identity binding
    private String userID; // Fetched from JSON or updated by Broker
    private String userType; // Fetched from JSON or updated by Broker
    private String lightState = "FREE"; // Updated based on sensor MQTT feed or programmed by broker
    private boolean lowLight = false; // Updated based on JSON or programmed by broker
    private String lightID = "bla bla"; // TODO : INITIALIZE IN CLASSROOM
    
    private double XLocation = -1.0;
    private double YLocation = -1.0;
    private String RGB; // Used to pass color settings to RasPi
    private String Behavior = "Distributed"; // Determines behavior (programmed via setter method by broker)
    private TCPListener tcpServer;
    private String dimSetting = "50,50,50"; // For FREE State
    private String fetchJSON; // URL to fetch the JSON file and parse
    
    // Values from JSON File
    private String userRGB;
    
    public MyLight() {
    	tcpServer = new TCPListener(this);
    	tcpServer.start();
        // notify new date each 5 second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fireResourcesChange(13);
            }
        }, 5000, 5000);
        // Create a parser for the configuration JSON file here
        // Used the parsed result to initialize all the variables (will prevent garbage returns on reads before writes)
    }

    @Override
    public ReadResponse read(int resourceid) {
        LOG.info("Read on Device Resource " + resourceid);
        switch (resourceid) {
        case 0:
            return ReadResponse.success(resourceid, getLightId());
        case 1:
            return ReadResponse.success(resourceid, getDeviceType());
        case 2:
            return ReadResponse.success(resourceid, getLightState());
        case 3:
            return ReadResponse.success(resourceid, getUserType());
        case 4:
            return ReadResponse.success(resourceid, getUserId());
        case 5:
            return ReadResponse.success(resourceid, getRGB());
        case 6:
            return ReadResponse.success(resourceid, getLightStatus());
        case 7:
        	return ReadResponse.success(resourceid, getGroup());
        case 8:
        	return ReadResponse.success(resourceid, getLocationX());
        case 9:
        	return ReadResponse.success(resourceid, getLocationY());
        case 10:
        	return ReadResponse.success(resourceid, getRoomID());
        case 11:
        	return ReadResponse.success(resourceid, getBehavior());
        default:
            return super.read(resourceid);
        }
    }

    @Override
    public ExecuteResponse execute(int resourceid, String params) {
        LOG.info("Execute on Device resource " + resourceid);
        if (params != null && params.length() != 0)
            System.out.println("\t params " + params);
        return ExecuteResponse.success();
    }

    @Override
    public WriteResponse write(int resourceid, LwM2mResource value) {
        LOG.info("Write on Device Resource " + resourceid + " value " + value);
        switch (resourceid) {
        case 8:
        	setLocationX((double) value.getValue());
        	return WriteResponse.success();
        case 9:
        	setLocationY((double) value.getValue());
        	return WriteResponse.success();
        case 5:
        	boolean success = setRGB((String) value.getValue());
        	if (success) {
        		LOG.info("Write to SenseHat successful \n");
        		return WriteResponse.success();
        	}
        	else {
        		LOG.info("SenseHat process cannot be found \n");
        		return WriteResponse.notFound();
        	}
        case 11:
        	setBehavior((String)value.getValue());
        	return WriteResponse.success();
        case 7:
        	setGroupID((int)value.getValue());
        	return WriteResponse.success();
        case 10:
        	setRoomID((String)value.getValue());
        	return WriteResponse.success();
        case 6:
        	setLowLight((boolean)value.getValue());
        	return WriteResponse.success();
        case 12:
        	setOwnershipPriority((String)value.getValue());
        	return WriteResponse.success();
        case 4:
        	setUserID((String)value.getValue());
        	return WriteResponse.success();
        default:
            return super.write(resourceid, value);
        }
    }

    // Getter Functions
    private String getLightId() {
        return lightID;
    }

    private String getDeviceType() {
        return "Light Device";
    }

    private String getLightState() {
        return lightState;
    }

    private String getUserType() {
        return userType;
    }

    private String getUserId() {
        return userID;
    }

    private String getRGB() {
    	return RGB;
    }
    
    private boolean getLightStatus(){
    	return lowLight;
    }
    
    private int getGroup() {
    	return groupID;
    }
    
    private double getLocationX() {
    	return XLocation;
    }
    
    private double getLocationY() {
    	return YLocation;
    }
    
    private String getRoomID() {
    	return roomID;
    }
    
    private String getBehavior() {
    	return Behavior;
    }
    
    // Setter functions
    private void setLocationX(double new_x) {
    	XLocation = new_x;
    }
    
    private void setLocationY(double new_y) {
    	YLocation = new_y;
    }
    
    private boolean setRGB(String new_RGB) {
    	RGB = new_RGB;
    	// Write the value to the sense hat process (Local TCP server)
    	TCPClient tcpClient = new TCPClient();
    	String command = "setrgb,"+new_RGB;
    	
    	boolean success = tcpClient.send(command);
    	
    	if (success)
    		return true;
    	else
    		return false;
    }
    
    private void setBehavior(String new_behavior){
    	LOG.info("Updating behavior to "+new_behavior+" by broker \n");
    	Behavior = new_behavior;
    }
    
    public void MQTTHandler(String sensorStatus) {
    	if (sensorStatus.equals("OCCUPIED")) {
    		this.setRGB(userRGB);
    	}
    	else if (sensorStatus.equals("FREE")) {
    		this.setRGB(dimSetting);
    	}
    }
    
    private void setGroupID(int new_groupID) {
    	LOG.info("Updating groupID to "+new_groupID);
    	groupID = new_groupID;
    }
    
    private void setRoomID(String new_roomID) {
    	LOG.info("Updating roomID to "+new_roomID);
    	roomID = new_roomID;
    }
    
    @SuppressWarnings("unused")
	private static String readAll(Reader rd) throws IOException {
        StringBuilder sb = new StringBuilder();
        int cp;
        while ((cp = rd.read()) != -1) {
          sb.append((char) cp);
        }
        return sb.toString();
      }
    
	private void setOwnershipPriority(String json_url) {
		LOG.info("Updating JSON url "+json_url);
    	fetchJSON = json_url;
    	// Code to fetch file hosted on HTTP
/*
    	InputStream is;
		
    	try {
			is = (InputStream) new URL(json_url).openStream();
			BufferedReader rd = new BufferedReader(new InputStreamReader(is));
			String json_content = readAll(rd);
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
*/
                
    	// TODO : Activate the HTTP reading 
    	try {
			String json_content = new String(Files.readAllBytes(Paths.get("/home/pi/example.json")));
			JSONArray arr = new JSONArray(json_content);
			JSONObject obj;
			LOG.info("Starting to parse json file ...");
			for (int i = 0; i < arr.length(); i++) {
				obj = arr.getJSONObject(i);
				int json_location_x = obj.getInt("user_location_x");
				int json_location_y = obj.getInt("user_location_y");
				if (json_location_x == (int)XLocation && json_location_y == (int)YLocation) {
					userRGB = obj.getString("light_color");
					userType = obj.getString("user_type");
					userID = obj.getString("user_id");
					lowLight = obj.getBoolean("low_light");
					LOG.info("Updating settings RGB = "+userRGB+" ID = "+"userID = " +userID);
				}
				
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			LOG.error("Parsing failed/File not found");
			e.printStackTrace();
		}
    }
    
    private void setUserID(String new_userID) {
    	LOG.info("Updating userID to "+new_userID);
    	userID = new_userID;
    }
    
    private void setLowLight(boolean new_lightStatus) {
    	lowLight = new_lightStatus;
    }
}