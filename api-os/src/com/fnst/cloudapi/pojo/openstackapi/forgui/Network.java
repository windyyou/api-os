package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.List;

public class Network {
	
//	@Resource
//	NetworkMapper networksMapper;
//	
//	@Resource
//	SubnetMapper subnetsMapper;

	private String id;
	private String name;
	private String status;
	private String createdAt;
	private Boolean managed;
	private Boolean admin_state_up;
	//other attibute
	private String tenant_id; 
	private String instance_id; 
	private String subnetId;
	private String portId;
	private String floatingipId;
	private List<String> subnetsId = new ArrayList<String>();
	private List<Subnet> subnets = new ArrayList<Subnet>();
	private List<Port> ports = new ArrayList<Port>();
	private List<FloatingIP> floatingIps = new ArrayList<FloatingIP>();
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedAt() {
		return this.createdAt;
	}
	
	public void setTenant_id(String tenant_id){
		this.tenant_id = tenant_id;
	}
	
	public String getTenant_id(){
		return this.tenant_id;
	}

	public List<Subnet> getSubnets() {
		return subnets;
	}

	public void setSubnets(List<Subnet> subnets) {
		this.subnets = subnets;
	}
	
	public void addSubnet(Subnet subnet){
		this.subnets.add(subnet);
	}

	
	public String getInstance_id() {
		return instance_id;
	}

	public void setInstance_id(String instance_id) {
		this.instance_id = instance_id;
	}

	public List<String> getSubnetsId() {
		return subnetsId;
	}

	public void setSubnetsId(List<String> subnetsId) {
		this.subnetsId = subnetsId;
	}
    
	public String getSubnetId() {
		return subnetId;
	}

	public void setSubnetId(String subnetId) {
		this.subnetId = subnetId;
	}

	public String getPortId() {
		return portId;
	}

	public void setPortId(String portId) {
		this.portId = portId;
	}


	public void addSubnetId(String id){
		this.subnetsId.add(id);
	}

	public List<Port> getPorts() {
		return ports;
	}

	public void setPorts(List<Port> ports) {
		this.ports = ports;
	}
	
	public void addPort(Port port){
		this.ports.add(port);
	}

	
	public String getFloatingipId() {
		return floatingipId;
	}

	public void setFloatingipId(String floatingipId) {
		this.floatingipId = floatingipId;
	}

	public List<FloatingIP> getFloatingIps() {
		return floatingIps;
	}

	public void setFloatingIps(List<FloatingIP> floatingIps) {
		this.floatingIps = floatingIps;
	}
	
	public void addFloatingIP(FloatingIP floatingIP){
		this.floatingIps.add(floatingIP);
	}

	public Boolean getManaged() {
		return managed;
	}

	public void setManaged(Boolean managed) {
		this.managed = managed;
	}

	public Boolean getAdmin_state_up() {
		return admin_state_up;
	}

	public void setAdmin_state_up(Boolean admin_state_up) {
		this.admin_state_up = admin_state_up;
	}
	
	
//	//subnets is not save!!!
//	public void saveDB(){
//		networksMapper.insertSelective(this);
//		networksMapper.insertInstancesNetworks(this);
//		networksMapper.insertTenantsNetworks(this);
//		
//	}
//	
//	//subnets is not save!!!
//	public void deleteDB(){
//		networksMapper.deleteByPrimaryKey(this.getId());	
//		networksMapper.deleteInstancesNetworks(this.getId());	
//		networksMapper.deleteTenantsNetworks(this.getId());	
//	}
	
}

