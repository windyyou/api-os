package com.fnst.cloudapi.controller.openstackapi;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.service.openstackapi.MonitorService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.ParamConstant;

public class MonitorController {

	@RequestMapping(value = "/monitor/instances", method = RequestMethod.POST)
	public String createMonitorObj(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String createBody, HttpServletResponse response) {

		if (null == createBody || createBody.isEmpty()) {
			ResourceBusinessException exception = new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0003"); // TODO//
																													// change//the/message
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return exception.getResponseMessage();
		}

		// TODO
		// check the createBoay
		try {
			MonitorService monitorService = OsApiServiceFactory.getMonitorService();
			monitorService.createMonitorObj(createBody, guiToken, response);
//			JsonHelper<Port, String> jsonHelp = new JsonHelper<Port, String>();
//			return jsonHelp.generateJsonBodySimple(port);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0006");
			return exception.getResponseMessage();
		}

		return "";
	}
	
	@RequestMapping(value = "/monitor/instances", method = RequestMethod.DELETE)
	public String deleteMonitorObj(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String createBody, HttpServletResponse response) {

		if (null == createBody || createBody.isEmpty()) {
			ResourceBusinessException exception = new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0003"); // TODO//
																													// change//the/message
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return exception.getResponseMessage();
		}

		// TODO
		// check the createBoay
		try {
			MonitorService monitorService = OsApiServiceFactory.getMonitorService();
			monitorService.deleteMonitorObj(createBody, guiToken, response);
//			JsonHelper<Port, String> jsonHelp = new JsonHelper<Port, String>();
//			return jsonHelp.generateJsonBodySimple(port);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0006");
			return exception.getResponseMessage();
		}

		return "";
	}
	
	@RequestMapping(value = "/monitor/notifications", method = RequestMethod.POST)
	public String createMonitorNotification(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String createBody, HttpServletResponse response) {

		if (null == createBody || createBody.isEmpty()) {
			ResourceBusinessException exception = new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0003"); // TODO//
																													// change//the/message
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return exception.getResponseMessage();
		}

		// TODO
		// check the createBoay
		try {
			MonitorService monitorService = OsApiServiceFactory.getMonitorService();
			monitorService.createMonitorNotification(createBody, guiToken, response);
//			JsonHelper<Port, String> jsonHelp = new JsonHelper<Port, String>();
//			return jsonHelp.generateJsonBodySimple(port);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0006");
			return exception.getResponseMessage();
		}

		return "";
	}
	
	@RequestMapping(value = "/monitor/notifications", method = RequestMethod.DELETE)
	public String deleteMonitorNotification(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String createBody, HttpServletResponse response) {

		if (null == createBody || createBody.isEmpty()) {
			ResourceBusinessException exception = new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0003"); // TODO//
																													// change//the/message
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return exception.getResponseMessage();
		}

		// TODO
		// check the createBoay
		try {
			MonitorService monitorService = OsApiServiceFactory.getMonitorService();
			monitorService.deleteMonitorNotification(createBody, guiToken, response);
//			JsonHelper<Port, String> jsonHelp = new JsonHelper<Port, String>();
//			return jsonHelp.generateJsonBodySimple(port);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0006");
			return exception.getResponseMessage();
		}

		return "";
	}
}
