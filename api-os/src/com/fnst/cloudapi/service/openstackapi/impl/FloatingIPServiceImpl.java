package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

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
import com.fnst.cloudapi.pojo.openstackapi.forgui.Instance;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceDetail;
import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroupRule;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.FloatingIPService;
import com.fnst.cloudapi.service.openstackapi.InstanceService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsBase;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

@Service
public class FloatingIPServiceImpl implements FloatingIPService{
	
	@Autowired
	private CloudConfig cloudconfig;
	
	private static final String TYPE_OF_INSTANCE = "instance";

	private HttpClientForOsRequest client=null;
    private int ERROR_HTTP_CODE = 400;

	public FloatingIPServiceImpl() {
		super();
		client = new HttpClientForOsRequest();
	}
	
	
	@Override
	public List<FloatingIP> getFloatingIPList(Map<String,String> paramMap,String tokenId, HttpServletResponse response) throws BusinessException{
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot=osClient.getToken();
	//	TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/floatingips", paramMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());


		List<FloatingIP> floatingIps;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				floatingIps = getFloatingIPs(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			try {
				floatingIps = getFloatingIPs(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0006");
		}

		return floatingIps;
	}
	
	@Override
	public FloatingIP getFloatingIP(String floatingIpId, String tokenId,HttpServletResponse response) throws BusinessException{
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot=osClient.getToken();
	//	TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/v2.0/floatingips/");
		sb.append(floatingIpId);
	
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(sb.toString(), headers);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());


		FloatingIP floatingIp = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode floatingipNode = rootNode.path(ResponseConstant.FLOATINGIP);
				floatingIp = getFloatingIPInfo(floatingipNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(sb.toString(), headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if(null != response)
			    response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode floatingipNode = rootNode.path(ResponseConstant.FLOATINGIP);
				floatingIp = getFloatingIPInfo(floatingipNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_GET_0006");
		}

		return floatingIp;
	}
	
	@Override
	public FloatingIP createFloatingIp(String createBody, String tokenId,HttpServletResponse response) throws BusinessException{
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot=osClient.getToken();
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2.0/floatingips", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		// Map<String, String> rs =client.httpDoGet(url, headers);
		Map<String, String> rs = client.httpDoPost(url, ot.getTokenid(),createBody);
		if (null == rs)
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_CREATE_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		FloatingIP floatingIp = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_CREATE_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode floatingIpNode = rootNode.path(ResponseConstant.FLOATINGIP);
				floatingIp = getFloatingIPInfo(floatingIpNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_FLOATING_CREATE_0003");
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
				JsonNode floatingIpNode = rootNode.path(ResponseConstant.FLOATINGIP);
				floatingIp = getFloatingIPInfo(floatingIpNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_NETWORK_FLOATING_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_NETWORK_FLOATING_CREATE_0006");
		}
		
		return floatingIp;
	}
	
	private List<FloatingIP> getFloatingIPs(Map<String, String> rs) throws JsonProcessingException, IOException, BusinessException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode floatingipsNode = rootNode.path(ResponseConstant.FLOATINGIPS);
		int floatingsCount = floatingipsNode.size();
		if (0 == floatingsCount)
			return null;
		
		List<FloatingIP> floatingIPs = new ArrayList<FloatingIP>();
		for (int index = 0; index < floatingsCount; ++index) {
			FloatingIP floatingIP = getFloatingIPInfo(floatingipsNode.get(index));
			floatingIPs.add(floatingIP);
		}
		
		return floatingIPs;
	}
	
	private FloatingIP getFloatingIPInfo(JsonNode floatingIPNode) throws BusinessException{
		if(null == floatingIPNode)
			return null;
		FloatingIP floatingIPInfo = new FloatingIP();

		floatingIPInfo.setNetworkId(floatingIPNode.path(ResponseConstant.FLOATING_NETWORK_ID).textValue());
		floatingIPInfo.setRouterId(floatingIPNode.path(ResponseConstant.ROUTER_ID).textValue());
		floatingIPInfo.setFixedIpAddress(floatingIPNode.path(ResponseConstant.FIXED_IP_ADDRESS).textValue());
		floatingIPInfo.setFloatingIpAddress(floatingIPNode.path(ResponseConstant.FLOATING_IP_ADDRESS).textValue());
		floatingIPInfo.setTenantId(floatingIPNode.path(ResponseConstant.TENANT_ID).textValue());
		floatingIPInfo.setStatus(floatingIPNode.path(ResponseConstant.STATUS).textValue());
		floatingIPInfo.setId(floatingIPNode.path(ResponseConstant.ID).textValue());
		
		return floatingIPInfo;
	}
	
	@Override
	/**
	 * for api   /floating-ips
	 */
	public List<FloatingIP> getFloatingIPExtList(Map<String, String> paramMap, String tokenId) throws BusinessException{
		
		//获取所有的instance,组装成 一个hashmap
		//key=ip, value=resource(HashMap)
		InstanceService inService= OsApiServiceFactory.getInstanceService();
		List<InstanceDetail> instanceList=inService.getInstanceList(null, tokenId,null);
		HashMap<String,HashMap> instanceInfo =  new HashMap<String, HashMap>();
		HashMap<String, String> resource = null;
		List<String> fpList = new ArrayList<String>();
		for(Instance instance:instanceList){
			fpList = instance.getFloatingIps();
		    if(fpList != null && !fpList.isEmpty()){
		    	for(String fp: fpList){
		    		resource = new HashMap<String, String>();
		    		resource.put(ResponseConstant.NAME, instance.getName());
		    		resource.put(ResponseConstant.ID, instance.getId());
		    		resource.put(ResponseConstant.RESOURCE_TYPE, TYPE_OF_INSTANCE);
		    		instanceInfo.put(fp, resource);
		    	}
		    }
		    
		}
		
		//获取所有网络的信息列表组成一个HashMap
		//key=net_id, value=网络信息
	   /* NetworkService netService = OsApiServiceFactory.getNetworkService();
	    List<Network> netList = netService.getNetworkList(paramMap, tokenId);
	    for(Network network:netList){
	    	
	    }*/
		
	
		// TODO
		// 取得bandwith,line等信息
		
		
		Map<String, String>  rs = getFloatingIPs(paramMap, tokenId);
		try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
				JsonNode floatingipsNode=rootNode.path(ResponseConstant.FLOATINGIPS);
				int floatingipsCount =floatingipsNode.size();
                if(0 == floatingipsCount)
                	return null;
                
                List<FloatingIP> list= new ArrayList<FloatingIP>();	
				for(JsonNode floatingIPNode:floatingipsNode){
					HashMap map = new HashMap();
					FloatingIP floatingIPExt = new FloatingIP();					
					floatingIPExt.setId(floatingIPNode.path(ResponseConstant.ID).textValue());
					floatingIPExt.setName("Fake Name");
//					floatingIPExt.setBandwidth("100 Mbps");
//					floatingIPExt.setLine("中国电信");
					floatingIPExt.setStatus(floatingIPNode.path(ResponseConstant.STATUS).textValue());
					floatingIPExt.setFloatingIpAddress(floatingIPNode.path(ResponseConstant.FLOATING_IP_ADDRESS).textValue());
//					floatingIPExt.setCreatedAt("2016-01-01");
//					HashMap re = instanceInfo.get(floatingIPNode.path(ResponseConstant.FLOATING_IP_ADDRESS).textValue())==null?
//							(new HashMap()):instanceInfo.get(floatingIPNode.path(ResponseConstant.FLOATING_IP_ADDRESS).textValue());
//					floatingIPExt.setResource(re);
					/*if(floatingIPNode.path("port_id").textValue()!=null&&!(floatingIPNode.path("port_id").textValue().equals(""))){
						
					}*/
					
					list.add(floatingIPExt);
					
			}
				
			return list;
		}
		catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
	/**
	 * 根据条件查询到所有的floating ip对象
	 * 并且对此对象不做任何处理
	 * @param paramMap
	 * @return
	 */
	private Map<String, String> getFloatingIPs(Map<String, String> paraMap, String tokenId){
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot=osClient.getToken();
		//todo 1: 通过guitokenid 取得实际，用户信息
        //AuthService	as = new AuthServiceImpl();	
        //as.GetTokenOS(guiTokenId);
		String region ="RegionOne";
		String url=ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();		
		url=RequestUrlHelper.createFullUrl(url+"/v2.0/floatingips", paraMap);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
		
		client =new HttpClientForOsRequest();
		Map<String, String>  rs = client.httpDoGet(url, headers);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		System.out.println("httpcode:"+rs.get("httpcode")); 
		System.out.println("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) > ERROR_HTTP_CODE){
			System.out.println("wo cha:request failed"); 
			return null;
		}
		return rs;
		
	}
	
	
	/**
	 * 根据floatingIP id获取额外的信息: 如从属的网络、绑定的端口信息等
	 * @param paraMap
	 * @return
	 */
	private String getFloatingIPExtInfo(String id){
		
		
		return null;
	}
	
}
