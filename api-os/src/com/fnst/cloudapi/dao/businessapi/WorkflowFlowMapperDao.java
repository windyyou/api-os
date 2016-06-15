package com.fnst.cloudapi.dao.businessapi;

import java.util.List;

import com.fnst.cloudapi.dao.common.SuperMapper;
import com.fnst.cloudapi.pojo.businessapi.workflow.WorkflowFlowMapper;


/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年5月31日 上午11:52:51 
* 
*/
public interface WorkflowFlowMapperDao extends SuperMapper<WorkflowFlowMapper,String> {
	
	public List<WorkflowFlowMapper> selectAll();

	public List<WorkflowFlowMapper> selectAllForPage(int start, int end);
	
	/**
	 * 根据flow的名字查找JBPM中对应的processID
	 * @param flowName
	 * @return
	 */
	public WorkflowFlowMapper selectProcessByFlowName(String flowName);

}
