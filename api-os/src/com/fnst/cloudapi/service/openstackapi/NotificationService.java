package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Notification;

public interface NotificationService {

	public List<Notification> getNotificationList(Map<String,String> paraMap,String tokenId);
}
