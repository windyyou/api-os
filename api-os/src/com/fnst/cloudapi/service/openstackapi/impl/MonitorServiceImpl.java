package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceDetail;
import com.fnst.cloudapi.pojo.openstackapi.forgui.MonitorObj;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.MonitorService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;

public class MonitorServiceImpl extends CloudConfigAndTokenHandler implements MonitorService{
	private HttpClientForOsRequest client = null;

	public MonitorServiceImpl() {

		this.client = new HttpClientForOsRequest();

	}
	
	@Override
	public void createMonitorObj(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException{
		
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_MONITOR, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/monitor/instance");

		HashMap<String, String> headers = new HashMap<String, String>();
	//	headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoPost(sb.toString(), headers,createBody);
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		InstanceDetail instanceDetail = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode resultNode = rootNode.path(ResponseConstant.RESULT);
				JsonNode failedNode = resultNode.path(ResponseConstant.FAILED);
				if(failedNode.size() > 0)
					throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0006");
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
		//	headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(sb.toString(), headers,createBody);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode resultNode = rootNode.path(ResponseConstant.RESULT);
				JsonNode failedNode = resultNode.path(ResponseConstant.FAILED);
				if(failedNode.size() > 0)
					throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0006");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_CREATE_0006");
		}
	}
	
	
	@Override
	public void deleteMonitorObj(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException{
		
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_MONITOR, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/monitor/instance");

		HashMap<String, String> headers = new HashMap<String, String>();
	//	headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoDelete(sb.toString(),headers,createBody);
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode resultNode = rootNode.path(ResponseConstant.RESULT);
				JsonNode failedNode = resultNode.path(ResponseConstant.FAILED);
				if(failedNode.size() > 0)
					throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0006");
				JsonNode notFoundNode = resultNode.path(ResponseConstant.NOT_FOUND);
				if(notFoundNode.size() > 0)
					throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0006");
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
		//	headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(sb.toString(), headers,createBody);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode resultNode = rootNode.path(ResponseConstant.RESULT);
				JsonNode failedNode = resultNode.path(ResponseConstant.FAILED);
				if(failedNode.size() > 0)
					throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0006");
				JsonNode notFoundNode = resultNode.path(ResponseConstant.NOT_FOUND);
				if(notFoundNode.size() > 0)
					throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0006");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0002");
		default:
			throw new ResourceBusinessException("CS_MONITOR_OBJECT_DELETE_0006");
		}
	}
	
	
	@Override
	public void createMonitorNotification(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException{
		
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_MONITOR, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/monitor/notification");

		HashMap<String, String> headers = new HashMap<String, String>();
	//	headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoPost(sb.toString(), headers,createBody);
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		InstanceDetail instanceDetail = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				if(false == isSuccessToSetNotification(rootNode))
					throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0006");
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
		//	headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(sb.toString(), headers,createBody);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				if(false == isSuccessToSetNotification(rootNode))
					throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0006");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_CREATE_0006");
		}
	}
	
	@Override
	public void deleteMonitorNotification(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException{
		
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_MONITOR, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/monitor/notification");

		HashMap<String, String> headers = new HashMap<String, String>();
	//	headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoDelete(sb.toString(), headers,createBody);
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		InstanceDetail instanceDetail = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				String flag = rootNode.path(ResponseConstant.RESULT).textValue();
				if("flase".equals(flag))
					throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0006");
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
		//	headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoDelete(sb.toString(), headers,createBody);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				String flag = rootNode.path(ResponseConstant.RESULT).textValue();
				if("flase".equals(flag))
					throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0006");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0002");
		default:
			throw new ResourceBusinessException("CS_MONITOR_NOTIFICATION_DELETE_0006");
		}
	}
	
	private boolean isSuccessToSetNotification(JsonNode rootNode){
		JsonNode resultNode = rootNode.path(ResponseConstant.RESULT);
		JsonNode emailNode = resultNode.path(ResponseConstant.EMAIL_LIST);
		JsonNode failedNode = emailNode.path(ResponseConstant.FAILED);
		if(failedNode.size() > 0)
			return false;
		JsonNode mobileNode = rootNode.path(ResponseConstant.MOBILE_LIST);
		failedNode = mobileNode.path(ResponseConstant.FAILED);
		if(failedNode.size() > 0)
			return false;
		return true;
	}
}
