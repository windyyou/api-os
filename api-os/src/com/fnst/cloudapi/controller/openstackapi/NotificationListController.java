package com.fnst.cloudapi.controller.openstackapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.NotificationList;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Terminal;
import com.fnst.cloudapi.service.openstackapi.NotificationListService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class NotificationListController {

	@Autowired
	private NotificationListService notificationListService;
	
	@RequestMapping("/notification-lists")
	public List<NotificationList> getNotificationLists(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
			@RequestParam(value="limit",defaultValue="") String limit,
			@RequestParam(value="name", defaultValue="") String name,
    		@RequestParam(value="terminal", defaultValue="") String terminalId) {
		
		Map<String,String> paramMap=null; 
    	
    	if(!"".equals(limit)){
    		paramMap=new HashMap<String,String>();
    		paramMap.put("limit", limit);
    	}
    	
    	if(!"".equals(name)){
    		if(paramMap==null) paramMap=new HashMap<String,String>();
    		paramMap.put("name", name);
    	}
    	
    	if(!"".equals(terminalId)){		
    		if(paramMap==null) paramMap=new HashMap<String,String>();
    		paramMap.put("terminalId", terminalId);
    	}
    	
		List<NotificationList> notificationLists = null;
		try {
			notificationLists = notificationListService.getNotificationLists(paramMap, null);
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return notificationLists;
		
	}
	
	@RequestMapping(value="/notification-lists", produces = {"application/json;charset=UTF-8"}, method=RequestMethod.POST)
	public String createNotificationList(@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String createBody, HttpServletResponse response) {
		
		if (null == createBody || createBody.isEmpty()){
			return null; // TODO throw exception
		}
		
		ObjectMapper mapper = new ObjectMapper();
		NotificationList notificationList = null;
		try {
			notificationList = mapper.readValue(createBody, NotificationList.class);
		} catch (Exception e) {
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_CREATE_0003");
			return exception.getResponseMessage();
		}
		
		try {
			notificationListService.insertNotificationList(notificationList);
			List<NotificationList> notificationLists = notificationListService.getNotificationLists(null, null);
			JsonHelper<List<NotificationList>, String> jsonHelp = new JsonHelper<List<NotificationList>, String>();
			return jsonHelp.generateJsonBodySimple(notificationLists);
		} catch (ResourceBusinessException e) {
			// TODO
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_CREATE_0006");
			return exception.getResponseMessage();
		}
		
	}
	
	@RequestMapping(value="/notification-lists/{id}", produces = {"application/json;charset=UTF-8"}, method=RequestMethod.GET)
	public String showNotificationList(@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response) {
		
		try {
			NotificationList notificationList = notificationListService.selectNotificationListById(id);
			JsonHelper<NotificationList, String> jsonHelp = new JsonHelper<NotificationList, String>();
			return jsonHelp.generateJsonBodySimple(notificationList);
		} catch (ResourceBusinessException e) {
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_SHOW_0006");
			return exception.getResponseMessage();
		}
		
	}
	
	@RequestMapping(value="/notification-lists/{id}", produces={"application/json;charset=UTF-8"}, method=RequestMethod.DELETE)
	public String deleteNotificationList(@RequestHeader(value=ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue="") String guiToken,
			@PathVariable String id, HttpServletResponse response) {
		
		try {
			notificationListService.deleteNotificationListById(id);
			JsonHelper<String, String> jsonHelp = new JsonHelper<String, String>();
			return jsonHelp.generateJsonBodySimple("success");
		} catch (ResourceBusinessException e) {
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_SHOW_0006");
			return exception.getResponseMessage();
		}
	}
	
	@RequestMapping(value="/notification-lists/{id}", produces={}, method=RequestMethod.PUT)
	public String updateNotificationList(@RequestHeader(value=ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue="") String guiToken,
			@RequestBody String updateBody, HttpServletResponse response) {
		
		if (null == updateBody || updateBody.isEmpty()){
			return null; // TODO throw exception
		}
		
		ObjectMapper mapper = new ObjectMapper();
		NotificationList notificationList = null;
		try {
			notificationList = mapper.readValue(updateBody, NotificationList.class);
		} catch (Exception e) {
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_CREATE_0003");
			return exception.getResponseMessage();
		}
		
		try {
			notificationListService.updateNotificationListById(notificationList);
			JsonHelper<NotificationList, String> jsonHelp = new JsonHelper<NotificationList, String>();
			return jsonHelp.generateJsonBodySimple(notificationList);
		} catch (ResourceBusinessException e) {
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO: handle exception
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_SHOW_0006");
			return exception.getResponseMessage();
		}
		
	}
	
	@RequestMapping(value="/notification-lists/{pid}/terminals", produces={"application/json;charset=UTF-8"}, method=RequestMethod.POST)
	public String createTerminal(@RequestHeader(value=ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue="") String guiToken,
			@PathVariable(value="pid") String pid, @RequestBody String createBody, HttpServletResponse response) {
		
		if (null == createBody || createBody.isEmpty()){
			return null; // TODO throw exception
		}
		
		ObjectMapper mapper = new ObjectMapper();
		Terminal terminal = null;
		try {
			terminal = mapper.readValue(createBody, Terminal.class);
			terminal.setNotificationListId(pid);
		} catch (Exception e) {
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_CREATE_0003");
			return exception.getResponseMessage();
		}
		
		try {
			notificationListService.insertTerminal(terminal);
			NotificationList notificationList = notificationListService.selectNotificationListById(pid);
			JsonHelper<NotificationList, String> jsonHelp = new JsonHelper<NotificationList, String>();
			return jsonHelp.generateJsonBodySimple(notificationList);
		} catch (ResourceBusinessException e) {
			// TODO: handle exception
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO: handle exception
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_SHOW_0006");
			return exception.getResponseMessage();
		}
		
	}
	
	@RequestMapping(value="/notification-lists/{pid}/terminals/{id}", produces={"application/json;charset=UTF-8"}, method=RequestMethod.DELETE)
	public String deleteTerminal(@RequestHeader(value=ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue="") String guiToken,
			@PathVariable(value="pid") String pid, @PathVariable(value="id") String id, HttpServletResponse response) {
		
		try {
			notificationListService.deleteTerminal(id);
			NotificationList notificationList = notificationListService.selectNotificationListById(pid);
			JsonHelper<NotificationList, String> jsonHelp = new JsonHelper<NotificationList, String>();
			return jsonHelp.generateJsonBodySimple(notificationList);
		} catch (ResourceBusinessException e) {
			// TODO: handle exception
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NOTIFICATIONLIST_SHOW_0006");
			return exception.getResponseMessage();
		}
		
	}
	
	
}
