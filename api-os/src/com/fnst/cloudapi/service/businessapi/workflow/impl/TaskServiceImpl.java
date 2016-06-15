package com.fnst.cloudapi.service.businessapi.workflow.impl;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.service.businessapi.workflow.TaskService;
import com.fnst.cloudapi.service.businessapi.workflow.WorkFlowBaseService;
import com.fnst.cloudapi.util.ResponseConstant;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月8日 上午10:33:01 
* 
*/

@Service
public class TaskServiceImpl extends WorkFlowBaseService implements TaskService{

	@Override
	public String getTasks(CloudUser user) {
		String url = super.url + "/query/runtime/task";
		Map map = workFlowHttpClientUtil.httpDoGet(url, user.getCode(),user.getPassword());
		System.out.println("http===================code"+map.get("httpcode"));
		return (String) map.get(ResponseConstant.JSONBODY);
	}

	
	public String doAction(CloudUser user, String taskId, String action) {
		String url = super.url + "/task/"+taskId+"/"+action;

		Map map = workFlowHttpClientUtil.httpDoGet(url, user.getCode(),user.getPassword());
		System.out.println("http===================code"+map.get("httpcode"));
		return (String) map.get(ResponseConstant.JSONBODY);
	}

}
