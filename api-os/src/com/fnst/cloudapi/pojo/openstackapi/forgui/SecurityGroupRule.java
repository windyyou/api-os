package com.fnst.cloudapi.pojo.openstackapi.forgui;

public class SecurityGroupRule {
	private String id;
	private String direction;
	private String ethertype;
	private String portRangeMax;
	private String portRangeMin;
	private String protocol;
	private String remoteGroupId;
	private String remoteIpPrefix;
	private String securityGroupId;
	private String tenantId;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	public String getEthertype() {
		return ethertype;
	}

	public void setEthertype(String ethertype) {
		this.ethertype = ethertype;
	}

	public String getPortRangeMax() {
		return portRangeMax;
	}

	public void setPortRangeMax(String portRangeMax) {
		this.portRangeMax = portRangeMax;
	}

	public String getPortRangeMin() {
		return portRangeMin;
	}

	public void setPortRangeMin(String portRangeMin) {
		this.portRangeMin = portRangeMin;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getRemoteGroupId() {
		return remoteGroupId;
	}

	public void setRemoteGroupId(String remoteGroupId) {
		this.remoteGroupId = remoteGroupId;
	}

	public String getRemoteIpPrefix() {
		return remoteIpPrefix;
	}

	public void setRemoteIpPrefix(String remoteIpPrefix) {
		this.remoteIpPrefix = remoteIpPrefix;
	}

	public String getSecurityGroupId() {
		return securityGroupId;
	}

	public void setSecurityGroupId(String securityGroupId) {
		this.securityGroupId = securityGroupId;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

}
