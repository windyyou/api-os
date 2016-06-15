package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.NotificationList;


/** 
* @author  hous.jy@cn.fujitsu.com (2016-6-2 9:43:38)
* 
*/
public interface NotificationListMapper extends SuperMapper<NotificationList,String> {
	
	public List<NotificationList> selectAll();
	
	public int deleteByUpdateDelFlag(String id);

}
