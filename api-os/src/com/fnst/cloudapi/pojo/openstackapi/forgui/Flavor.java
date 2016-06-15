package com.fnst.cloudapi.pojo.openstackapi.forgui;

public class Flavor {

	private String id;
	private String name;
	private Integer ram;
	private Integer vcpus;
	private Integer disk;
	private Integer swap;
	private String rxtx_factor;

   // "OS-FLV-EXT-DATA:ephemeral": ephemeral,
   // "os-flavor-access:is_public": is_public,
    
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
	
	public void setRam(Integer ram){
		this.ram = ram;
	}
	
	public Integer getRam(){
		return this.ram;
	}
	
	public void setVcpus(Integer vcpus){
		this.vcpus = vcpus;
	}
	
	public Integer getVcpus(){
		return this.vcpus;
	}
	
	public void setDisk(Integer disk){
		this.disk = disk;
	}
	
	public Integer getDisk(){
		return this.disk;
	}
	
	public void setSwap(Integer swap){
		this.swap = swap;
	}
	
	public Integer getSwap(){
		return this.swap;
	}
	
	public void setRxtx_factor(String rxtx_factor){
		this.rxtx_factor = rxtx_factor;
	}
	
	public String getRxtx_factor(){
		return this.rxtx_factor;
	}
	
}
