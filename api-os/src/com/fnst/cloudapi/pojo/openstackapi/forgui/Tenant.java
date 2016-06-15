package com.fnst.cloudapi.pojo.openstackapi.forgui;

public class Tenant {

	private String id;
	private String name;
	private String description;
	private Boolean enabled;
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getId(){
		return this.id;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public void setDescription(String description){
		this.description = description;
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void setEnabled(Boolean enabled){
		this.enabled = enabled;
	}
	
	public Boolean getEnabled(){
		return this.enabled;
	}
}
