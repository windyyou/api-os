package com.fnst.cloudapi.pojo.openstackapi.forgui;


public class Subnet {
	
//	 @Resource
//     NetworkMapper networksMapper;
//	 
//	 @Resource
//     SubnetMapper subnetsMapper;


	private String id;
	private String name;	
	private String segment;	
	private String ipVersion;	
	private String gateway;
	private String gateway_ip;
	private Boolean enable_dhcp;
	private Boolean dhcp;
	private String network_id;
	private String createdAt;
	private String cidr;
	//no database table's column
	private Network network;
	private String tenant_id;
	private String ip_version;
	
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

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedAt() {
		return this.createdAt;
	}

	public String getSegment() {
		return segment;
	}

	public void setSegment(String segment) {
		this.segment = segment;
	}

	public String getIpVersion() {
		return ipVersion;
	}

	public void setIpVersion(String ipVersion) {
		this.ipVersion = ipVersion;
	}

	public String getGateway() {
		return gateway;
	}

	public void setGateway(String gateway) {
		this.gateway = gateway;
	}

	public Network getNetwork() {
		return network;
	}

	public void setNetwork(Network network) {
		this.network = network;
	}

	public void setCidr(String cidr){
		this.cidr = cidr;
	}
	
	public String getCidr(){
		return this.cidr;
	}
	
	public void setTenant_id(String tenant_id){
		this.tenant_id = tenant_id;
	}
	
	public String getTenant_id(){
		return this.tenant_id;
	}
	
	public void setNetwork_id(String network_id){
		this.network_id = network_id;
	}
	
	public String getNetwork_id(){
		return this.network_id;
	}
	
	public String getIp_version() {
		return ip_version;
	}

	public void setIp_version(String ip_version) {
		this.ip_version = ip_version;
	}
	
//	public void setNetwork() {
//		Network network = new Network();
//		network = networksMapper.selectByPrimaryKey(this.getNetwork_id());
//		this.network = network;
//	}
	
//	public void setOtherAttribute(String tenant_id){
//		this.setTenant_id(tenant_id);
//		this.setNetwork();
//	}
	
//	public void saveDB(){
//		subnetsMapper.insertSelective(this);		
//	}
    
	public String getGateway_ip() {
		return gateway_ip;
	}

	public Boolean getDhcp() {
		return dhcp;
	}

	public void setDhcp(Boolean dhcp) {
		this.dhcp = dhcp;
	}

	public void setGateway_ip(String gateway_ip) {
		this.gateway_ip = gateway_ip;
	}

	public Boolean getEnable_dhcp() {
		return enable_dhcp;
	}

	public void setEnable_dhcp(Boolean enable_dhcp) {
		this.enable_dhcp = enable_dhcp;
	}
	
	

}

