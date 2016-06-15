package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.NotificationList;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Terminal;

public interface NotificationListService {
	
	public List<NotificationList> getNotificationLists(Map<String, String> paraMap, String tokenId) throws BusinessException;
	
	public int insertNotificationList(NotificationList notificationList) throws BusinessException;
	
	public NotificationList selectNotificationListById(String id) throws BusinessException;
	
	public int deleteNotificationListById(String id) throws BusinessException;
	
	public int updateNotificationListById(NotificationList notificationList) throws BusinessException;
	
	public int insertTerminal(Terminal terminal) throws BusinessException;
	
	public int deleteTerminal(String id) throws BusinessException;
}
