package com.fnst.cloudapi.service.businessapi.workflow;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;

import com.fnst.cloudapi.dao.businessapi.WorkflowFlowMapperDao;
import com.fnst.cloudapi.pojo.businessapi.workflow.WorkflowFlowMapper;
import com.fnst.cloudapi.util.http.pool.OSHttpClientUtil;
import com.fnst.cloudapi.util.http.pool.WorkFlowHttpClientUtil;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月6日 下午4:14:23 
* 
*/
public abstract class WorkFlowBaseService {
	
	private static final String  urlPrefix = "runtime";
			
	//workflow部署的url
	@Value("#{ propertyConfigurer['wf.url'] }")
	public String url;
		
	//workflow部署的ID
	@Value("#{ propertyConfigurer['wf.deploymentId'] }")
	public String deploymentID;
	
	@Resource
    private WorkflowFlowMapperDao workflowFlowMapperDao;
	
	@Resource
	protected WorkFlowHttpClientUtil workFlowHttpClientUtil;
	
	
	/**
	 * 根据流程的名字，获取JBPM中的process id
	 * @param flowName
	 * @return
	 * @throws Exception
	 */
	protected String getProcessId(String flowName) throws Exception{
		WorkflowFlowMapper workflow = workflowFlowMapperDao.selectProcessByFlowName(flowName);
		if(workflow == null){
		    throw new Exception(flowName + " has no process");
		}
		return workflow.getJbpmProcessDefId();
	}
	
	
	/**
	 * 根据配置信息，得到JBPM部署的的URL
	 * @return
	 */
	protected String getDeploymentUrl(){
		
		return url+"/"+urlPrefix+"/"+deploymentID;
	}
	
	/**
	 * 从request的paramMap中获取指定的值
	 * @param paramMap
	 * @param key
	 * @return
	 */
	protected String getParamValue(Map paramMap, String key){
		String[] value = (String[]) paramMap.get(key);
		if (value.length > 0){
			return value[0];
		}
		return "";
	}

}
