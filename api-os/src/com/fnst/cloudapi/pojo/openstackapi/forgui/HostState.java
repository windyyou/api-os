package com.fnst.cloudapi.pojo.openstackapi.forgui;

public class HostState {
    private int ramSize;
    private int vcpus;
    private int resState;
    
    public void setRamSize(int ramSize){
    	this.ramSize = ramSize;
    }
    
    public int getRamSize(){
    	return this.ramSize;
    }
    
    public void setVcpus(int vcpus){
    	this.vcpus = vcpus;
    }
    
    public int getVcpus(){
    	return this.vcpus;
    }
    
    public void setResState(int resState){
    	this.resState = resState;
    }
    
    public int getResState(){
    	return this.resState;
    }
    
}
