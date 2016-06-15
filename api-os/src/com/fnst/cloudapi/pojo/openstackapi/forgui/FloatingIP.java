package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;


//@JsonInclude(value=Include.NON_NULL)
public class FloatingIP {
	
	private class Resource{
		private String id;
		private String name;
		private String type;
		
		Resource(){};
		Resource(String id,String name,String type){
			this.id = id;
			this.name = name;
			this.type = type;
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
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
		
		
		
	}
	private String id;
	private String tenantId;
	private String status;
	private String routerId;
	private String networkId;
	private String instanceId;
	private String fixedIpAddress;
	//for API /floating-ips & /floatingip
	private String floatingIpAddress;
	private String portId;
	private String type;
	private Resource resource;
	
	//for API /floating-ips
	private String name;
//	private String bandwidth;
//	private String line;
//	private HashMap<String,String> resource;
	private String createddAt;
	
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
    
	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public void setTenantId(String tenantId){
		this.tenantId = tenantId;
	}
	
	public String getTenantId(){
		return this.tenantId;
	}
	
	public void setStatus(String status){
		this.status = status;
	}
	
	public String getStatus(){
		return this.status;
	}
	
	public void setRouterId(String routerId){
		this.routerId = routerId;
	}
	
	public String getRouterId(){
		return this.routerId;
	}
	
	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	
	public void setFixedIpAddress(String fixedIpAddress){
		this.fixedIpAddress = fixedIpAddress;
	}
	
	public String getFixedIpAddress(){
		return this.fixedIpAddress;
	}
	
	public void setFloatingIpAddress(String floatingIpAddress){
		this.floatingIpAddress = floatingIpAddress;
	}
	
	public String getFloatingIpAddress(){
		return this.floatingIpAddress;
	}
	
	public void setPortId(String portId){
		this.portId = portId;
	}
	
	public String getPortId(){
		return this.portId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getCreateddAt() {
		return createddAt;
	}

	public void setCreateddAt(String createddAt) {
		this.createddAt = createddAt;
	}

	public Resource getResource() {
		return resource;
	}

	public void setResource(Resource resource) {
		this.resource = resource;
	}
	
	public String getResourceId(){
		return this.resource.getId();
	}

	public String getResourceName(){
		return this.resource.getName();
	}
	
	public String getResourceType(){
		return this.resource.getType();
	}
	
	public void addResource(String id,String name,String type){
		this.resource = new Resource(id,name,type);
	}

	
}
