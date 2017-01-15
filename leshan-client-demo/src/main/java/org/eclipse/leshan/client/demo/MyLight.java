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

    private static final Logger LOG = LoggerFactory.getLogger(MyDevice.class);
    
    private int groupID = -1; // Initialize via a set statement
    private String roomID = "0"; // Initial value
    private String behavior = "broker";
    private float XLocation = (float) -1.0;
    private float YLocation = (float) -1.0;
    
    public MyLight() {
        // notify new date each 5 second
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                fireResourcesChange(13);
            }
        }, 5000, 5000);
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
            return ReadResponse.success(resourceid, getLightColor());
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
        // Implement writes later
        default:
            return super.write(resourceid, value);
        }
    }

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

    private String getLightColor() {
    	return "(r,g,b) tuple";
    }
    
    private boolean getLightStatus(){
    	return true;
    }
    
    private int getGroup() {
    	return groupID;
    }
    
    private float getLocationX() {
    	return XLocation;
    }
    
    private float getLocationY() {
    	return YLocation;
    }
    
    private String getRoomID() {
    	return roomID;
    }
    
    private String getBehavior() {
    	return behavior;
    }

}