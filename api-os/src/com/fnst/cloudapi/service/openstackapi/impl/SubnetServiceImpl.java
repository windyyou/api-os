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
import com.fnst.cloudapi.json.forgui.SubnetJSON;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.NetworkService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.SubnetService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class SubnetServiceImpl extends CloudConfigAndTokenHandler implements SubnetService {
	private HttpClientForOsRequest client = null;

	public SubnetServiceImpl() {

		this.client = new HttpClientForOsRequest();

	}

	/**
	 * get this users subnet list
	 */

	@Override
	public List<Subnet> getSubnetList(Map<String, String> paramMap, String guiTokenId, HttpServletResponse response)
			throws BusinessException {

		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/subnets", paramMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, ot.getTokenid());
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");// TODO
																				// throw
																				// exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		List<Subnet> subnets = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				subnets = getSubnets(rs, guiTokenId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			try {
				subnets = getSubnets(rs, guiTokenId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0006");
		}

		return subnets;
	}

	@Override
	public Subnet getSubnet(String subnetId, String guiTokenId, HttpServletResponse response) throws BusinessException {
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		// String url=ot.getEndPoint(TokenOs.EP_TYPE_NETWORK,
		// region).getPublicURL();
		// url=url+"/v2.0/networks/" + NetworkId;
		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/v2.0/subnets/");
		sb.append(subnetId);
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());
		Map<String, String> rs = client.httpDoGet(sb.toString(), ot.getTokenid());
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");// TODO
																				// throw
																				// exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		Subnet subnet = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode subnetNode = rootNode.path(ResponseConstant.SUBNET);
				subnet = getSubnetInfo(subnetNode, guiTokenId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");
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
				JsonNode subnetNode = rootNode.path(ResponseConstant.SUBNET);
				subnet = getSubnetInfo(subnetNode, guiTokenId);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0006");
		}

		return subnet;
	}

	@Override
	public Subnet createSubnet(String createBody, String tokenId, HttpServletResponse response)
			throws BusinessException {
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";
		// String url=ot.getEndPoint(TokenOs.EP_TYPE_NETWORK,
		// region).getPublicURL();
		// url=url+"/v2.0/networks/" + NetworkId;
		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());
		Map<String, String> rs = client.httpDoPost(url + "/v2.0/subnets", ot.getTokenid(), createBody);
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0003");

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		Subnet subnet = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode subnetNode = rootNode.path(ResponseConstant.SUBNET);
				subnet = getSubnetInfo(subnetNode, ot.getTokenid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(url + "/v2.0/subnets", ot.getTokenid(), createBody);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode subnetNode = rootNode.path(ResponseConstant.SUBNET);
				subnet = getSubnetInfo(subnetNode, ot.getTokenid());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0006");
		}
		return subnet;
	}

	private String getSubNetCIDR() {
		// TODO
		// get the next sub net it by the current max subnet of local subnet db
		// the sub net mask is 255.255.0.0
		String cidr = "";

		int iFirstOctal = 254; // TODO change it later
		int iSecondOctal = 254;
		int iMaxOctal = 254;

		int currentMaxID = 0; // get the currentMaxID from local subnet db
		if (currentMaxID >= iSecondOctal) {
			int mod = currentMaxID % iMaxOctal;
			int quotient = currentMaxID / iMaxOctal;
			iFirstOctal -= quotient;
			iSecondOctal -= mod;
		} else
			iSecondOctal -= currentMaxID;
		cidr = String.format("%s.%s.0.0/16", iFirstOctal, iSecondOctal);
		return cidr;
	}

	private String generateBody(String network_id, String name, String cidr, String tenant_id, String ip_version) {

		SubnetJSON subnetJSON = new SubnetJSON();
		subnetJSON.setSubnetInfo(network_id, name, cidr, tenant_id, ip_version);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(subnetJSON);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}

	private List<Subnet> filterSubnet(Map<String, String> paramMap, List<Subnet> subnets) {
		if (null == paramMap || 0 == paramMap.size())
			return subnets;

		String subnetName = paramMap.get(ParamConstant.NAME);
		if (null != subnetName && !"".equals(subnetName)) {
			for (Subnet subnet : subnets) {
				if (subnetName.equals(subnet.getName())) {
					List<Subnet> goodsubnets = new ArrayList<Subnet>();
					goodsubnets.add(subnet);
					return goodsubnets;
				}
			}
		}

		String strLimit = paramMap.get(ParamConstant.LIMIT);
		if (null != strLimit && !"".equals(strLimit)) {
			try {
				int limit = Integer.parseInt(strLimit);
				if (limit >= subnets.size())
					return subnets;
				return subnets.subList(0, limit);
			} catch (Exception e) {
				// TODO
				return subnets;
			}
		}

		return subnets;
	}

	private List<Subnet> getSubnets(Map<String, String> rs, String tokenId)
			throws JsonProcessingException, IOException, BusinessException {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode subnetsNode = rootNode.path(ResponseConstant.SUBNETS);
		int subnetsCount = subnetsNode.size();
		if (0 == subnetsCount)
			return null;

		List<Subnet> subnets = new ArrayList<Subnet>();
		for (int index = 0; index < subnetsCount; ++index) {
			Subnet subnet = getSubnetInfo(subnetsNode.get(index), tokenId);
			subnets.add(subnet);
		}

		return subnets;
	}

	private Subnet getSubnetInfo(JsonNode subnetNode, String tokenId) throws BusinessException {
		if (null == subnetNode)
			return null;
		Subnet subnet = new Subnet();
		subnet.setName(subnetNode.path(ResponseConstant.NAME).textValue());
		subnet.setId(subnetNode.path(ResponseConstant.ID).textValue());
		subnet.setIpVersion(Integer.toString(subnetNode.path(ResponseConstant.IP_VERSION).intValue()));
		subnet.setGateway(subnetNode.path(ResponseConstant.GATEWAY_IP).textValue());
		subnet.setCidr(subnetNode.path(ResponseConstant.CIDR).textValue());
		subnet.setNetwork_id(subnetNode.path(ResponseConstant.NETWORK_ID).textValue());
		// NetworkService nwService = OsApiServiceFactory.getNetworkService();
		// Network network = nwService.getNetwork(networkId, tokenId, null);
		// subnet.setNetwork(network);

		return subnet;
	}
}
