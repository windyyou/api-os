package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Notification;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.NotificationService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.DateHelper;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class NotificationServiceImpl extends CloudConfigAndTokenHandler implements NotificationService {
	
	private HttpClientForOsRequest client = null;
	private int NORMAL_RESPONSE_CODE_200 = 200;
	
	public NotificationServiceImpl() {
		super();
		this.client = new HttpClientForOsRequest();
	}

	@Override
	public List<Notification> getNotificationList(Map<String, String> paraMap, String tokenId) {
		
		//@TODO get the data form database
		List<Notification> list = null;
		
		return list;
	}
	

}
