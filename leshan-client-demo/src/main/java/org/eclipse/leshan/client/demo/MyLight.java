package org.eclipse.leshan.client.demo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.leshan.client.resource.BaseInstanceEnabler;
import org.eclipse.leshan.core.model.ResourceModel.Type;
import org.eclipse.leshan.core.node.LwM2mResource;
import org.eclipse.leshan.core.response.ExecuteResponse;
import org.eclipse.leshan.core.response.ReadResponse;
import org.eclipse.leshan.core.response.WriteResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLight extends BaseInstanceEnabler {

    private static final Logger LOG = LoggerFactory.getLogger(MyLight.class);
    
    private int groupID = -1; // Initialize via a set statement
    private String roomID = "0"; // Initial value
    private double XLocation = -1.0;
    private double YLocation = -1.0;
    private String RGB; // Used to pass color settings to RasPi
    private String Behavior = "Broker"; // Determines behavior (programmed via setter method by broker)
    
    public MyLight() {
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
        default:
            return super.write(resourceid, value);
        }
    }

    // Getter Functions
    private String getLightId() {
        return "Not implemented yet, Id = 0";
    }

    private String getDeviceType() {
        return "Light Device";
    }

    private String getLightState() {
        return "Will return USED or FREE (based on pl) TODO";
    }

    private String getUserType() {
        return "Returns USER 1";
    }

    private String getUserId() {
        return "Return ID of User";
    }

    private String getRGB() {
    	return RGB;
    }
    
    private boolean getLightStatus(){
    	return true;
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
}