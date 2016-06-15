package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.List;

public class Volume {
	private String id;
	private String name;
	private String status;
	private String size;
	private String type;
	private String createdAt;
    private String instanceId;
    private String device;
    private String backupId;
    private List<Instance> instances;
    private List<Backup> backups;
    
    public Volume(){
    	this.id = new String();
    	this.name = new String();
    	this.status = new String();
    	this.size = new String();
    	this.createdAt = new String();
    	this.instances = new ArrayList<Instance>();
    	this.backups = new ArrayList<Backup>();
    }
	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

    
	public List<Instance> getInstances() {
		return instances;
	}

	public void setInstances(List<Instance> instance) {
		this.instances = instance;
	}
	
	public void addInstance(Instance instance){
		this.instances.add(instance);
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
	public String getBackupId() {
		return backupId;
	}
	
	public void setBackupId(String backupId) {
		this.backupId = backupId;
	}
	
	public List<Backup> getBackups() {
		return backups;
	}
	
	public void setBackups(List<Backup> backups) {
		this.backups = backups;
	}

	public void addBackup(Backup backup){
		this.backups.add(backup);
	}
	
}
