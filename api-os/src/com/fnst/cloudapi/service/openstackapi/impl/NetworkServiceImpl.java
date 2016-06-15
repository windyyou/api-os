package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.json.forgui.NetworkJSON;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Port;
import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroup;
import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroupRule;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.NetworkService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.SubnetService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class NetworkServiceImpl extends CloudConfigAndTokenHandler implements NetworkService {
	private HttpClientForOsRequest client = null;

	public NetworkServiceImpl() {

		this.client = new HttpClientForOsRequest();

	}

	@Override
	public List<Network> getNetworkList(Map<String, String> paramMap, String guiTokenId,HttpServletResponse response) throws BusinessException{

		//Firstly, get the networks from local db
		//if not, get the network from openstack
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/networks", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoGet(url, ot.getTokenid());
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_GET_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		List<Network> networks = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				networks = getNetworks(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				networks = getNetworks(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_GET_0006");
		}
		
		return networks;
	}

	@Override
	public Network getNetwork(String networkId, String guiTokenId,HttpServletResponse response) throws BusinessException {
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo
		String region = "RegionOne";

		// String url=ot.getEndPoint(TokenOs.EP_TYPE_NETWORK,
		// region).getPublicURL();
		// url=url+"/v2.0/networks/" + NetworkId;
		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/os-networks/");
		sb.append(networkId);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoGet(sb.toString(), ot.getTokenid());
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		Network network = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode networkNode = rootNode.path(ResponseConstant.NETWORK);
				network = getNetworkInfo(networkNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode networkNode = rootNode.path(ResponseConstant.NETWORK);
				network = getNetworkInfo(networkNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_GET_0006");
		}
		
		return network;
	}

	@Override
	public Network createNetwork(String createBody, String guiTokenId,HttpServletResponse response) throws BusinessException {
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);
//		String tenant_id = ""; // get the tenantId、tenantName from local DB by
								// the guiTokenId
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		String tenant_name = "net_name_" + df.format(new Date()); // TODO change
//																	// it later
//		String name = "";
//		if (null != paramMap)
//			name = paramMap.get(ParamConstant.NAME);
//		if (null == name || "".equals(name))
//			name = String.format("%s_Network", tenant_name);

		String region = "RegionOne"; // we should get the regioninfo by the
										// guiTokenId
		TokenOs ot = super.osToken;
		// token should have Regioninfo
		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/networks", null);

	//	String networkBody = generateBody(name, tenant_id);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoPost(url, headers, createBody);
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		Network network = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_CREATE_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode networkNode = rootNode.path(ResponseConstant.NETWORK);
				network = getNetworkInfo(networkNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(url, headers,createBody);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode networkNode = rootNode.path(ResponseConstant.NETWORK);
				network = getNetworkInfo(networkNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_CREATE_0006");
		}
		
		return network;
	}
	
	@Override
	public List<SecurityGroup> getSecurityGroupList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException
	{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/security-groups", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoGet(url, ot.getTokenid());
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		List<SecurityGroup> securityGroups = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				securityGroups = getSecurityGroups(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				securityGroups = getSecurityGroups(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0006");
		}
		
		return securityGroups;
	}
	
	@Override
	public SecurityGroup getSecurityGroup(String securityGroupId,String tokenId,HttpServletResponse response) throws BusinessException{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/v2.0/security-groups/");
		sb.append(securityGroupId);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoGet(sb.toString(), ot.getTokenid());
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		SecurityGroup securityGroup = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode securityGroupNode = rootNode.path(ResponseConstant.SECURITY_GROUP);
				securityGroup = getSecurityGroupInfo(securityGroupNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(sb.toString(), headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode securityGroupNode = rootNode.path(ResponseConstant.SECURITY_GROUP);
				securityGroup = getSecurityGroupInfo(securityGroupNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0006");
		}
		return securityGroup;
	}
	
	@Override
	public SecurityGroup createSecurityGroup(String createBody,String tokenId,HttpServletResponse response) throws BusinessException{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/security-groups", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoPost(url, ot.getTokenid(),createBody);
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		SecurityGroup securityGroup = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_CREATE_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode securityGroupNode = rootNode.path(ResponseConstant.SECURITY_GROUP);
				securityGroup = getSecurityGroupInfo(securityGroupNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0003");
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
				JsonNode securityGroupNode = rootNode.path(ResponseConstant.SECURITY_GROUP);
				securityGroup = getSecurityGroupInfo(securityGroupNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0006");
		}
		
		return securityGroup;
	}
	
	@Override
	public List<SecurityGroupRule> getSecurityGroupRuleList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/security-group-rules", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoGet(url, ot.getTokenid());
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		List<SecurityGroupRule> securityGroupRules = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				securityGroupRules = getSecurityGroupRules(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
		    httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				securityGroupRules = getSecurityGroupRules(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0006");
		}
		
		return securityGroupRules;
	}
	
	@Override
	public SecurityGroupRule createSecurityGroupRule(String createBody,String tokenId,HttpServletResponse response) throws BusinessException{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/security-group-rules", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoPost(url, ot.getTokenid(),createBody);
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		SecurityGroupRule securityGroupRule = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_CREATE_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode securityGroupRuleNode = rootNode.path(ResponseConstant.SECURITY_GROUP_RULE);
				securityGroupRule = getSecurityGroupRuleInfo(securityGroupRuleNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0003");
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
				JsonNode securityGroupRuleNode = rootNode.path(ResponseConstant.SECURITY_GROUP_RULE);
				securityGroupRule = getSecurityGroupRuleInfo(securityGroupRuleNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0006");
		}
		
		return securityGroupRule;
	}
	
	@Override
	public List<Port> getPortList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException
	{
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne"; // we should get the regioninfo by the guiTokenId
		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/ports", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, ot.getTokenid());
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		Port port = null;
        List<Port> ports = new ArrayList<Port>();
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode portsNode = rootNode.path(ResponseConstant.PORTS);
				int size = portsNode.size();
				for(int i = 0; i < size; ++i){
					port = getPortInfo(portsNode.get(i),tokenId);
					ports.add(port);
				}
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode portsNode = rootNode.path(ResponseConstant.PORTS);
				int size = portsNode.size();
				for(int i = 0; i < size; ++i){
					port = getPortInfo(portsNode.get(i),tokenId);
					ports.add(port);
				}
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0006");
		}
		
		return ports;
	}
	
	@Override
	public Port getPort(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException
	{
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne"; // we should get the regioninfo by the guiTokenId
		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/ports/");
		sb.append(paramMap.get(ParamConstant.ID));
		url = RequestUrlHelper.createFullUrl(url+"/v2.0/ports", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, ot.getTokenid());
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		Port port = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode portNode = rootNode.path(ResponseConstant.PORT);
				port = getPortInfo(portNode,tokenId);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode portNode = rootNode.path(ResponseConstant.PORT);
				port = getPortInfo(portNode,tokenId);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0006");
		}
		
		return port;
	}
	
	@Override
	public Port createPort(String createBody, String tokenId,HttpServletResponse response) throws BusinessException{
		if(null == createBody)
			return null;
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne"; // we should get the regioninfo by the guiTokenId
		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url+"/ports", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());
		Map<String, String> rs = client.httpDoPost(url, headers,createBody);
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		Port port = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_CREATE_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode portNode = rootNode.path(ResponseConstant.PORT);
				port = getPortInfo(portNode,tokenId);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_PORT_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(url, headers,createBody);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode portNode = rootNode.path(ResponseConstant.PORT);
				port = getPortInfo(portNode,tokenId);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_PORT_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_PORT_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_PORT_GET_0006");
		}
		
		return port;
	}
	
	private Port getPortInfo(JsonNode portNode, String tokenId){
		if(null == portNode)
			return null;
		Port port = new Port();
		port.setId(portNode.path(ResponseConstant.ID).textValue());
		port.setName(portNode.path(ResponseConstant.NAME).textValue());
		port.setStatus(portNode.path(ResponseConstant.STATUS).textValue());
		port.setMacAddress(portNode.path(ResponseConstant.MAC_ADDRESS).textValue());
		port.setDeviceOwner(portNode.path(ResponseConstant.DEVICE_OWNER).textValue());
		port.setNetworkId(portNode.path(ResponseConstant.NETWORK_ID).textValue());
		JsonNode fixedIpsNode = portNode.path(ResponseConstant.FIXED_IPS);
		if(null == fixedIpsNode)
			return port;
		int fixedipsCount = fixedIpsNode.size();
		if(0 == fixedipsCount)
			return port;
		String subnetId = fixedIpsNode.get(0).path(ResponseConstant.SUBNET_ID) .textValue();
		port.setSubnetId(subnetId);
		return port;
	}
	
//	private String generateBody(String name, String tenant_id) {
//
//		NetworkJSON networkJSON = new NetworkJSON();
//		networkJSON.setNetworkInfo(name, tenant_id);
//
//		ObjectMapper mapper = new ObjectMapper();
//		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
//		mapper.setSerializationInclusion(Include.NON_NULL);
//		mapper.setSerializationInclusion(Include.NON_EMPTY);
//		String jsonStr = "";
//		try {
//			jsonStr = mapper.writeValueAsString(networkJSON);
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		return jsonStr;
//	}
	
	private List<Network> getNetworks(Map<String, String> rs) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode networksNode = rootNode.path(ResponseConstant.NETWORKS);
		int networksCount = networksNode.size();
		if (0 == networksCount)
			return null;
		List<Network> networks = new ArrayList<Network>();
		for (int index = 0; index < networksCount; ++index) {
			Network network = getNetworkInfo(networksNode.get(index));
			networks.add(network);
		}
		return networks;
	}
	
	private Network getNetworkInfo(JsonNode networkNode){
		if (null == networkNode)
			return null;
		Network network = new Network();
		network.setStatus(networkNode.path(ResponseConstant.STATUS).textValue());
		network.setName(networkNode.path(ResponseConstant.NAME).textValue());
		network.setId(networkNode.path(ResponseConstant.ID).textValue());
		network.setTenant_id(networkNode.path(ResponseConstant.TENANT_ID).textValue());
		JsonNode subnetsNode = networkNode.path(ResponseConstant.SUBNETS);
		if(null != subnetsNode){
			int subnetsCount = subnetsNode.size();
			for(int index = 0 ; index < subnetsCount; ++index){
				network.addSubnetId(subnetsNode.get(index).textValue());
			}
		}
		return network;
	}
	
	private List<SecurityGroup> getSecurityGroups(Map<String, String> rs) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode securityGroupsNode = rootNode.path(ResponseConstant.SECURITY_GROUPS);
		int securityGroupsCount = securityGroupsNode.size();
		if (0 == securityGroupsCount)
			return null;
		List<SecurityGroup> securityGroups = new ArrayList<SecurityGroup>();
		for (int index = 0; index < securityGroupsCount; ++index) {
			SecurityGroup securityGroup = getSecurityGroupInfo(securityGroupsNode.get(index));
			securityGroups.add(securityGroup);
		}
		return securityGroups;
	}
	
	private SecurityGroup getSecurityGroupInfo(JsonNode securityGroupNode){
		if (null == securityGroupNode)
			return null;
		SecurityGroup securityGroup = new SecurityGroup();
		securityGroup.setId(securityGroupNode.path(ResponseConstant.ID).textValue());
		securityGroup.setName(securityGroupNode.path(ResponseConstant.NAME).textValue());
		securityGroup.setDescription(securityGroupNode.path(ResponseConstant.DESCRIPTION).textValue());
		securityGroup.setTenantId(securityGroupNode.path(ResponseConstant.TENANT_ID).textValue());
		JsonNode securityGropRulesNode =  securityGroupNode.path(ResponseConstant.SECURITY_GROUP_RULES);
		if(null != securityGropRulesNode){
			int securityGroupRulesCount = securityGropRulesNode.size();
			for(int index = 0; index < securityGroupRulesCount; ++index){
				SecurityGroupRule securityGroupRule = getSecurityGroupRuleInfo(securityGropRulesNode.get(index));
				if(null == securityGroupRule)
					continue;
				securityGroup.addSecurityGroupRule(securityGroupRule);
			}
		}
		return securityGroup;
	}
	
	private List<SecurityGroupRule> getSecurityGroupRules(Map<String, String> rs) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode securityGropRulesNode = rootNode.path(ResponseConstant.SECURITY_GROUP_RULES);
		int securityGroupRulesCount = securityGropRulesNode.size();
		
		if (0 == securityGroupRulesCount)
			return null;
		
		List<SecurityGroupRule> securityGroupRules = new ArrayList<SecurityGroupRule>();
		for(int index = 0; index < securityGroupRulesCount; ++index){
			SecurityGroupRule securityGroupRule = getSecurityGroupRuleInfo(securityGropRulesNode.get(index));
			if(null == securityGroupRule)
				continue;
			securityGroupRules.add(securityGroupRule);
		}
		return securityGroupRules;
	}
	
	private SecurityGroupRule getSecurityGroupRuleInfo(JsonNode securityGroupRuleNode){
		if(null == securityGroupRuleNode)
			return null;
		SecurityGroupRule securityGroupRule = new SecurityGroupRule();
		securityGroupRule.setId(securityGroupRuleNode.path(ResponseConstant.ID).textValue());
		securityGroupRule.setDirection(securityGroupRuleNode.path(ResponseConstant.DIRECTION).textValue());
		securityGroupRule.setEthertype(securityGroupRuleNode.path(ResponseConstant.ETHERTYPE).textValue());
		securityGroupRule.setPortRangeMax(Integer.toString(securityGroupRuleNode.path(ResponseConstant.PORT_RANGE_MAX).intValue()));
		securityGroupRule.setPortRangeMin(Integer.toString(securityGroupRuleNode.path(ResponseConstant.PORT_RANGE_MIN).intValue()));
		securityGroupRule.setProtocol(securityGroupRuleNode.path(ResponseConstant.PROTOCOL).textValue());
		securityGroupRule.setRemoteGroupId(securityGroupRuleNode.path(ResponseConstant.REMOTE_GROUP_ID).textValue());
		securityGroupRule.setRemoteIpPrefix(securityGroupRuleNode.path(ResponseConstant.REMOTE_IP_PREFIX).textValue());
		securityGroupRule.setSecurityGroupId(securityGroupRuleNode.path(ResponseConstant.SECURITY_GROUP_ID).textValue());
		securityGroupRule.setTenantId(securityGroupRuleNode.path(ResponseConstant.TENANT_ID).textValue());
		
		return securityGroupRule;
	}
	
	 private List<Network> filterNetwork(Map<String,String> paramMap,List<Network> networks){
			if(null == paramMap || 0 == paramMap.size())
				return networks;

			String networkName = paramMap.get(ParamConstant.NAME);
			if(null != networkName && !"".equals(networkName)){
			    for(Network network:networks){  
			        if(networkName.equals(network.getName())){
			        	List<Network> goodNetworks= new ArrayList<Network>();	
			        	goodNetworks.add(network);
			        	return goodNetworks;
			        }
			    } 
			}
			
			String strLimit = paramMap.get(ParamConstant.LIMIT);
			if(null != strLimit && !"".equals(strLimit)){
				try{
					int limit = Integer.parseInt(strLimit);
					if(limit >= networks.size())
						return networks;
					return networks.subList(0, limit);
				}catch(Exception e){
					//TODO
					return networks;
				}
			}
			
	    	return networks;
		}
	 
	 private List<Network> setSubnetInfos(List<Network> networks,String token) throws BusinessException{
		 if(null == networks || 0 == networks.size())
			 return null;
		 
		 SubnetService snService= OsApiServiceFactory.getSubnetService();
	     List<Subnet> subnets = snService.getSubnetList(null, token,null);
	     if(null == subnets || 0 == subnets.size())
	    	 return null;
	     
	     List<Network> networkwithSubnets = new ArrayList<Network>();
	     
	     for(Network network : networks){
	    	 for(Subnet subnet : subnets){
	    		 if(network.getId().equals(subnet.getNetwork_id())){
	    			 network.addSubnet(subnet);
	    		 }
	    	 }
	    	 networkwithSubnets.add(network);
	     }
		 return networkwithSubnets;
	 }

}
