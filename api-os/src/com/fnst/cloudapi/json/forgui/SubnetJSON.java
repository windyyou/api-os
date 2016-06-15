package com.fnst.cloudapi.json.forgui;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;

public class SubnetJSON {
    private Subnet subnet;
    
    public SubnetJSON(){
    	this.subnet = new Subnet();
    }
    
    public void setSubnetInfo(String network_id,String name,String cidr,String tenant_id,String ip_version){
    	this.subnet.setNetwork_id(network_id);
    	this.subnet.setName(name);
    	this.subnet.setCidr(cidr);
    	this.subnet.setTenant_id(tenant_id);
    	this.subnet.setIp_version(ip_version);
    }
    
    public void setSubnetInfo(Subnet subnetInfo){
    	this.subnet.setNetwork_id(subnetInfo.getNetwork_id());
    	this.subnet.setName(subnetInfo.getName());
    	this.subnet.setIp_version(subnetInfo.getIpVersion());
    	this.subnet.setCidr(subnetInfo.getCidr());
    	this.subnet.setEnable_dhcp(subnetInfo.getDhcp());;
    	this.subnet.setGateway_ip(subnetInfo.getGateway());
    }
}
