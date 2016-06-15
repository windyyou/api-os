package com.fnst.cloudapi.service.businessapi.workflow;
/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月8日 下午1:50:20 
* 
*/
public enum  ActionEnum {
	
	
	PROCESS_START("start","启动"),//启动一个流程实例
	
	//==================Task 动作===========================
	
	TASK_ACTIVATE("activate","激活"), //激活task
	TASK_CLAIM("claim","声明"), //对task声明
	TASK_COMPLETE("complete","完成"), //完成task
	TASK_DELEGATE("delegate","委托"), //委托task
	TASK_FAIL("fail","失败"), 
	//TASK_FORWARD("forward"), 
	//TASK_NOMINATE("nominate"), 
	//TASK_RELEASE("release"), 
	//TASK_RESUME("resume"), 
	TASK_SKIP("skip","跳过"), 
	TASK_START("start","开始");
	//TASK_STOP("stop"), 
	//TASK_SUSPEND("suspend");
    
	private String action;
	private String action_CN;
	
	
	private ActionEnum(String action,String action_CN){
		this.action = action;
		this.action_CN = action_CN;
	}
	
	public String getAction(){
		return this.action;
	}

	public String getAction_CN() {
		return action_CN;
	}

	

}