package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.CloudConfig;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.FloatingIP;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Router;
import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroupRule;
import com.fnst.cloudapi.service.openstackapi.RouterService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsBase;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

@Service
public class RouterServiceImpl implements RouterService{
	
	private HttpClientForOsRequest client;
	
	RouterServiceImpl(){
		 this.client =new HttpClientForOsRequest();
	}
	@Autowired
	private CloudConfig cloudconfig;

	@Override
	public List<Router> getRouterList(Map<String, String> paramMap, String tokenId, HttpServletResponse response) throws BusinessException {
		
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot=osClient.getToken();
	//	TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/routers", paramMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());


		List<Router> routers = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				routers = getRouters(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			try {
				routers = getRouters(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0006");
		}

		return routers;
	}

	@Override
	public Router getRouter(String routerId, String tokenId,HttpServletResponse response) throws BusinessException{
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot=osClient.getToken();
	//	TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/v2.0/routers/");
		sb.append(routerId);
	
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(sb.toString(), headers);

		Router router = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode routerNode = rootNode.path(ResponseConstant.ROUTER);
				router = getRouterInfo(routerNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if(null != response)
			    response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode routerNode = rootNode.path(ResponseConstant.ROUTER);
				router = getRouterInfo(routerNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0006");
		}

		return router;
	}
	
	@Override
	public Router createRouter (String createBody,String tokenId,HttpServletResponse response) throws BusinessException{
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot=osClient.getToken();
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/routers", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoPost(url, ot.getTokenid(),createBody);
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		Router router = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_CREATE_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode routerNode = rootNode.path(ResponseConstant.ROUTER);
				router = getRouterInfo(routerNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(url, ot.getTokenid(),createBody);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode routerNode = rootNode.path(ResponseConstant.ROUTER);
				router = getRouterInfo(routerNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0006");
		}
		
		return router;
	}
	
	private List<Router> getRouters(Map<String, String> rs) throws JsonProcessingException, IOException, BusinessException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode routersNode = rootNode.path(ResponseConstant.ROUTERS);
		int routersCount = routersNode.size();
		if (0 == routersCount)
			return null;
		
		List<Router> routers = new ArrayList<Router>();
		for (int index = 0; index < routersCount; ++index) {
			Router router = getRouterInfo(routersNode.get(index));
			routers.add(router);
		}
		
		return routers;
	}
	
	private Router getRouterInfo(JsonNode routerNode) throws BusinessException{
		if(null == routerNode)
			return null;
		Router router = new Router();

		router.setId(routerNode.path(ResponseConstant.ID).textValue());
		router.setName(routerNode.path(ResponseConstant.NAME).textValue());
		router.setStatus(routerNode.path(ResponseConstant.STATUS).textValue());
		JsonNode gateway = routerNode.path(ResponseConstant.EXTERNAL_GATEWAY_INFO);
		if( gateway != null)
		   router.setGateway(gateway.path(ResponseConstant.EBABLE_SNAT).booleanValue()==true ? ResponseConstant.ENABLE:ResponseConstant.DISABLE);
		
		return router;
	}
}
