package com.fnst.cloudapi.controller.openstackapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Notification;
import com.fnst.cloudapi.service.openstackapi.NotificationService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class NotificationController {

	@RequestMapping(value = "/notifications", method = RequestMethod.GET)
	public List<Notification> getNotificationList(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.LIMIT, defaultValue = "") String limit,
			@RequestParam(value = ParamConstant.TYPE, defaultValue = "") String type,
			@RequestParam(value = ParamConstant.READ, defaultValue = "") String read) {

		Map<String, String> paramMap = null;

		if (!"".equals(limit)) {
			paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.LIMIT, limit);
		}

		if (!"".equals(type)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.TYPE, type);
		}

		if (!"".equals(read)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.READ, read);
		}

		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
		// request

		// @TODO 2. guitoken should be checked, timeout or not

		NotificationService notificationService = OsApiServiceFactory.getNotificationService();
		List<Notification> list = notificationService.getNotificationList(paramMap, guiToken);

		return list;

	}
	
}
