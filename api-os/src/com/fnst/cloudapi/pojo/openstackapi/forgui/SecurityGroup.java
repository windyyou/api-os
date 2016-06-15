package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.List;

public class SecurityGroup {
	private String id;
	private String name;
	private String description;
	private String tenantId;
	private String createdAt;
	private List<SecurityGroupRule> securityGroupRules = new ArrayList<SecurityGroupRule>();
	
	public SecurityGroup(){
		this.id = new String();
		this.name = new String();
		this.description = new String();
		this.createdAt = new String();
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
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getTenantId() {
		return tenantId;
	}
	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}
	public List<SecurityGroupRule> getSecurityGroupRules() {
		return securityGroupRules;
	}
	public void setSecurityGroupRules(List<SecurityGroupRule> securityGroupRules) {
		this.securityGroupRules = securityGroupRules;
	}
	
	public void addSecurityGroupRule(SecurityGroupRule secutiryGroupRule){
		this.securityGroupRules.add(secutiryGroupRule);
	}
	public String getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}
	
}
