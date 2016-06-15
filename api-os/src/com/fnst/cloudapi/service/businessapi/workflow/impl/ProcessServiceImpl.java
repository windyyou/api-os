package com.fnst.cloudapi.service.businessapi.workflow.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.stereotype.Service;

import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.service.businessapi.workflow.ProcessService;
import com.fnst.cloudapi.service.businessapi.workflow.WorkFlowBaseService;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.pool.WorkFlowHttpClientUtil;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月8日 上午10:35:06 
* 
*/

@Service
public class ProcessServiceImpl extends WorkFlowBaseService implements ProcessService{

	@Override
	public String startProcess(CloudUser user,String flowName, Map<String, String[]> inParam) throws Exception {
		
		String processId = getProcessId(flowName);
		String url = getDeploymentUrl() + "/"+"process/"+processId+"/start";
		//拼接url,形如: /kie-wb/rest/runtime/fnst:yuncloud:1.0/process/yuncloud.applyResouce/start/?map_resouceName=computer
		String param = "";
		for (String key : inParam.keySet()) {
			param += "map_"+key+"="+getParamValue(inParam,key)+"&";
		}
		if(!param.isEmpty()){
			url += "?"+param.substring(0,param.length()-1);
		}
		
		Map map = workFlowHttpClientUtil.httpDoPost(url, user.getCode(),user.getPassword(), null);
		System.out.println("http===================code"+map.get("httpcode"));
		return (String) map.get(ResponseConstant.JSONBODY);
	}

	

}
