package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.HashMap;
import java.util.Map;

public class HardQuota {

	private String userId;
	private String tenantId;
	private Map<String,QuotaDetails> resQuota;

	public void setUserId(String userId){
		this.userId = userId;
	}
	
	public String getUserId(){
		return this.userId;
	}
	
	public void setTenantId(String tenantId){
		this.tenantId = tenantId;
	}
	
	public String getTenantId(){
		return this.tenantId;
	}
	
	public void addQuotaDetails(String quotaName,QuotaDetails quotaDetails){
		if(null == this.resQuota)
			this.resQuota = new HashMap<String,QuotaDetails>();
		this.resQuota.put(quotaName, quotaDetails);
	}
	
	public Map<String,QuotaDetails> getQuotaDetails(){
		return this.resQuota;
	}
}
