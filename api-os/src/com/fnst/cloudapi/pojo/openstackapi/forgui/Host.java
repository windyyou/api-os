package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.HashMap;
import java.util.Map;

public class Host {
	
    private String hostName;
    private String serviceName;
    private String zoneName;
    private Map<Integer,HostState> hostState;

    
    public void setHostName(String hostName){
    	this.hostName = hostName;
    }
    
    public String getHostName(){
    	return this.hostName;
    }
    
    public void setServiceName(String serviceName){
    	this.serviceName = serviceName;
    }
    
    public String getServiceName(){
    	return this.serviceName;
    }
    
    public void setZoneName(String zoneName){
    	this.zoneName = zoneName;
    }
    
    public String getZoneName(){
    	return this.zoneName;
    }
    
    public void addHostState(Integer state,HostState host){
    	if(null == this.hostState)
    		this.hostState = new HashMap<Integer,HostState>();
    	hostState.put(state, host);
    }
    
    public Map<Integer,HostState> getHostsState(){
    	return this.hostState;
    }
}
