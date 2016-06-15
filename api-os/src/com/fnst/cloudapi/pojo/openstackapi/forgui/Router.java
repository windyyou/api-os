package com.fnst.cloudapi.pojo.openstackapi.forgui;


public class Router {
	
	private String id;
	private String name;
	private String gateway;
	private String floatingIp;
	private String createdAt;
	private String status;
	
	public Router(){
		this.id = new String();
		this.name = new String();
		this.gateway = new String();
		this.floatingIp = new String();
		this.createdAt = new String();
		this.status = new String();
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
	public String getGateway() {
		return gateway;
	}
	public void setGateway(String gateway) {
		this.gateway = gateway;
	}
	public String getFloatingIp() {
		return floatingIp;
	}
	public void setFloatingIp(String floatingIp) {
		this.floatingIp = floatingIp;
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
}

