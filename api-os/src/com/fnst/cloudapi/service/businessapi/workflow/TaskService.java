package com.fnst.cloudapi.service.businessapi.workflow;

import com.fnst.cloudapi.pojo.common.CloudUser;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月8日 上午10:32:16 
* 
*/
public interface TaskService {
	
	public String getTasks(CloudUser user);
	public String doAction(CloudUser user, String taskId, String action);

}
