package com.fnst.cloudapi.pojo.businessapi.workflow;

import java.util.Date;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月7日 下午6:16:04 
* 
*/
public class WorkflowFlowMapper {
	
	private String id;
	private String flowName;
	private String JbpmProcessDefId;
	private Date createdAt;
	private Date updatedAt;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFlowName() {
		return flowName;
	}
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}
	public String getJbpmProcessDefId() {
		return JbpmProcessDefId;
	}
	public void setJbpmProcessDefId(String jbpmProcessDefId) {
		JbpmProcessDefId = jbpmProcessDefId;
	}
	public Date getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	public Date getUpdatedAt() {
		return updatedAt;
	}
	public void setUpdatedAt(Date updatedAt) {
		this.updatedAt = updatedAt;
	}
	

    
}
