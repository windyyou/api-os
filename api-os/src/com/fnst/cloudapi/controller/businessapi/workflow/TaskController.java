package com.fnst.cloudapi.controller.businessapi.workflow;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.service.businessapi.workflow.TaskService;
import com.fnst.cloudapi.util.ParamConstant;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月6日 上午11:33:16 
* 任务
*/

@RestController
public class TaskController {
	
	@Resource
	private TaskService taskService;
	
	/**
	 * 获取当前用户下的所有task
	 * @param guiToken
	 * @param limit
	 * @param name
	 * @param status
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "workflow/tasks", method = RequestMethod.GET)
	public String getTasks(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = "limit", defaultValue = "") String limit,
			@RequestParam(value = "status", defaultValue = "") String status,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		CloudUser user = (CloudUser) request.getAttribute("cloudUser");
		System.out.println("userName========"+user.getName());
		
		return taskService.getTasks(user);
		
	}
	
	/**
	 * 执行Task下某一个动作,这些动作包括：开始、声明、完成等等
	 * @param guiToken
	 * @param taskId
	 * @param action
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "workflow/task/{taskId}/{action}", method = RequestMethod.POST)
	public String doAction(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String taskId,
			@PathVariable String action,
			HttpServletRequest request,
			HttpServletResponse response) {
		
		CloudUser user = (CloudUser) request.getAttribute("cloudUser");
		System.out.println("userName========"+user.getName());
		
		return taskService.doAction(user, taskId, action);
		
	}

}
