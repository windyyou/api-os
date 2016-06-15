package com.fnst.cloudapi.service.openstackapi.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Host;
import com.fnst.cloudapi.pojo.openstackapi.forgui.HostState;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.HostService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class HostServiceImpl extends CloudConfigAndTokenHandler implements HostService{
	private HttpClientForOsRequest httpClient=null;
    private int ERROR_HTTP_CODE = 400;

    
	public HostServiceImpl() {
		httpClient = new HttpClientForOsRequest();
	}
	
	@Override
	public List<Host> getHostList(Map<String,String> paramMap,String guiTokenId) {
	
		//todo 1: 通过guitokenid 取得实际，用户信息
        //AuthService	as = new AuthServiceImpl();	
        //as.GetTokenOS(guiTokenId);
		   
		TokenOs ot = super.osToken;
	    //token should have Regioninfo
		
		String region ="RegionOne";//we should get the regioninfo by the guiTokenId
		
		String url=ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();		
		url=RequestUrlHelper.createFullUrl(url+"/os-hosts", null);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
		
		
		Map<String, String>  rs = httpClient.httpDoGet(url, headers);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		System.out.println("httpcode:"+rs.get("httpcode")); 
		System.out.println("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) > ERROR_HTTP_CODE){
			System.out.println("wo cha:request failed"); 
			return null;
		}
	    
		try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
				JsonNode hostsNode=rootNode.path(ResponseConstant.HOSTS);
				int hostsCount =hostsNode.size();
                if(0 == hostsCount)
                	return null;
                
                List<Host> list= new ArrayList<Host>();	
				for(int index = 0; index < hostsCount; ++index){
					Host hostInfo = new Host();
					JsonNode hostNode = hostsNode.get(index);
		
					hostInfo.setHostName(hostNode.path(ResponseConstant.HOST_NAME).textValue());
				    hostInfo.setServiceName(hostNode.path(ResponseConstant.SERVICE_NAME).textValue());
				    hostInfo.setZoneName(hostNode.path(ResponseConstant.ZONE_NAME).textValue());
				    
				    list.add(hostInfo);
			}
	
			return filterHost(paramMap,list);
		}
		catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public Host getHostDetails(String hostName,String guiTokenId){
		//todo 1: 通过guitokenid 取得实际，用户信息
        //AuthService	as = new AuthServiceImpl();	
        //as.GetTokenOS(guiTokenId);
		   
		TokenOs ot = super.osToken;
	    //token should have Regioninfo
		
		
		String region ="RegionOne";//we should get the regioninfo by the guiTokenId
		
		String url=ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();		
		url=RequestUrlHelper.createFullUrl(url+"/os-hosts/"+hostName, null);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
		
		
		Map<String, String>  rs = httpClient.httpDoGet(url, headers);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		System.out.println("httpcode:"+rs.get("httpcode")); 
		System.out.println("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) > ERROR_HTTP_CODE){
			System.out.println("wo cha:request failed"); 
			return null;
		}
		
		try {
			  ObjectMapper mapper = new ObjectMapper();
			  JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
			  JsonNode resourcesNode=rootNode.path(ResponseConstant.HOST);
			  int resourcesCount =resourcesNode.size();
              if(0 == resourcesCount)
            	return null;
            
              Host host = new Host();
              host.setHostName(hostName);
              host.setServiceName(TokenOs.EP_TYPE_COMPUTE);
			  for(int index = 0; index < resourcesCount; ++index){
				 JsonNode resourceNode = resourcesNode.get(index);
				 JsonNode resDetail = resourceNode.get(ResponseConstant.RESOURCE);
				 if(resDetail.path(ResponseConstant.PROJECT).textValue().equals(ResponseConstant.PROJECT_TOTAL)){
					HostState hostState = new HostState();
					hostState.setResState(ParamConstant.TOTAL_RES);
					hostState.setVcpus(resDetail.path(ResponseConstant.CPU).intValue());
					hostState.setRamSize(resDetail.path(ResponseConstant.MEMORY).intValue());
					host.addHostState(ParamConstant.TOTAL_RES, hostState);
					
				 }else if(resDetail.path(ResponseConstant.PROJECT).textValue().equals(ResponseConstant.PROJECT_USED)){
					HostState hostState = new HostState();
					hostState.setResState(ParamConstant.USED_RES);
					hostState.setVcpus(resDetail.path(ResponseConstant.CPU).intValue());
					hostState.setRamSize(resDetail.path(ResponseConstant.MEMORY).intValue());
					host.addHostState(ParamConstant.USED_RES,hostState);
				 }			    
		   }
		  return host;
	
	     } catch(Exception e){
		    // TODO Auto-generated catch block
		    e.printStackTrace();
	    }
		
		return null;
	}
	
//	//get the cpu info and memory info of all compute hosts
//	private List<Host> getHostsDetails(String guiTokenId){
//		Map<String,String> paramMap = new HashMap<String,String>();
//		paramMap.put(ParamConstant.SERVICE_NAME, TokenOs.EP_TYPE_COMPUTE);
//		List<Host> hosts = getHostList(paramMap,guiTokenId);
//		if(null == hosts || 0 == hosts.size())
//			return null;
//		List<Host> hostsDetails = new ArrayList<Host>();
//		for(Host host:hosts){
//			Host detail = getHostDetails(host.getHostName(),guiTokenId);
//			if(null == detail)
//				continue;
//			hostsDetails.add(detail);
//		}
//		return hostsDetails;
//	}
	
    private List<Host> filterHost(Map<String,String> paramMap,List<Host> hosts){
		if(null == paramMap || 0 == paramMap.size())
			return hosts;

		String hostName = paramMap.get(ParamConstant.NAME);
		if(null != hostName && !"".equals(hostName)){
		    for(Host host:hosts){  
		        if(hostName.equals(host.getHostName())){
		        	List<Host> goodHosts= new ArrayList<Host>();	
		        	goodHosts.add(host);
		        	return goodHosts;
		        }
		    } 
		}
		
		String strLimit = paramMap.get(ParamConstant.LIMIT);
		if(null != strLimit && !"".equals(strLimit)){
			try{
				int limit = Integer.parseInt(strLimit);
				if(limit >= hosts.size())
					return hosts;
				return hosts.subList(0, limit);
			}catch(Exception e){
				//TODO
				return hosts;
			}
		}
	
		String serviceName = paramMap.get(ParamConstant.SERVICE_NAME);
		if(null != serviceName && !"".equals(serviceName)){
			List<Host> goodHosts = new ArrayList<Host>();
			for(Host host:hosts){  
		        if(serviceName.equals(host.getServiceName()))
		        	goodHosts.add(host);
			}
			return goodHosts;
		}
		
    	return hosts;
	}
}
