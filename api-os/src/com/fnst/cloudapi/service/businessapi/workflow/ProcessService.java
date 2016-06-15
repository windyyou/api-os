package com.fnst.cloudapi.service.businessapi.workflow;

import java.util.Map;

import com.fnst.cloudapi.pojo.common.CloudUser;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月8日 上午10:34:50 
* 
*/
public interface ProcessService {
	
	/**
	 * 启动一个过程实例
	 * @return
	 */
	public String startProcess(CloudUser user ,String flowName, Map<String, String[]> inParam) throws Exception;

}
