package com.fnst.cloudapi.controller.businessapi.workflow;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.service.businessapi.workflow.ProcessService;
import com.fnst.cloudapi.util.ParamConstant;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月7日 上午9:35:17 
* 流程控制
* 流程实例的CRUD
*/

@RestController
public class ProcessController {
	
	@Resource
	private ProcessService processService;
	
	
	/**
	 * 启动一个流程实例
	 * @param guiToken
	 * @param flowName 流程名 ,示例: openstack.instance.create
	 * @param action
	 * @param response
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping(value = "workflow/process/start", method = RequestMethod.POST)
	public String getIntancesList(
			HttpServletRequest request,
			HttpServletResponse response,
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = "flowName", defaultValue = "") String flowName
			) throws Exception {
		HashMap inMap=new HashMap(request.getParameterMap());
		CloudUser user = (CloudUser) request.getAttribute("cloudUser");
		System.out.println("flowName==============="+flowName);
		inMap.remove("flowName");
		return processService.startProcess(user,flowName, inMap);
		
	}
	
	

}
