package com.fnst.cloudapi.service.openstackapi.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.binding.MapperMethod.ParamMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.json.forgui.FlavorJSON;
import com.fnst.cloudapi.json.forgui.KeypairJSON;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.DiskType;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Flavor;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Host;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Keypair;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.FlavorService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class FlavorServiceImpl extends CloudConfigAndTokenHandler implements FlavorService{
	
	private HttpClientForOsRequest httpClient=null;
    private int NORMAL_RESPONSE_CODE = 200;
   
    
	public FlavorServiceImpl() {
		httpClient = new HttpClientForOsRequest();
	}
	
	@Override
	public List<Flavor> getFlavorList(Map<String,String> paramMap,String tokenId){
		//todo 1: 通过guitokenid 取得实际，用户信息
        //AuthService	as = new AuthServiceImpl();	
        //as.GetTokenOS(guiTokenId);
		
		//TODO first, get the list from local db
		TokenOs ot = super.osToken;
	    //token should have Regioninfo
		
		String region ="RegionOne";//we should get the regioninfo by the guiTokenId
		
		String url=ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();		
		url=RequestUrlHelper.createFullUrl(url+"/flavors/detail", null);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
		
		
		Map<String, String>  rs = httpClient.httpDoGet(url, headers);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		System.out.println("httpcode:"+rs.get("httpcode")); 
		System.out.println("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) != NORMAL_RESPONSE_CODE){
			System.out.println("wo cha:request failed"); 
			return null;
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
			JsonNode flavorsNode=rootNode.path(ResponseConstant.FLAVORS);
			int flavorsCount =flavorsNode.size();
            if(0 == flavorsCount)
            	return null;
            
            List<Flavor> list= new ArrayList<Flavor>();	
			for(int index = 0; index < flavorsCount; ++index){
				Flavor flavorInfo = new Flavor();
				JsonNode flavorNode = flavorsNode.get(index);
	
				flavorInfo.setId(flavorNode.path(ResponseConstant.ID).textValue());
				flavorInfo.setName(flavorNode.path(ResponseConstant.NAME).textValue());
				flavorInfo.setRam(flavorNode.path(ResponseConstant.RAM).intValue());
				flavorInfo.setVcpus(flavorNode.path(ResponseConstant.VCPUS).intValue());
				flavorInfo.setDisk(flavorNode.path(ResponseConstant.DISK).intValue());
				
			    list.add(flavorInfo);
		     }
			return filterFlavor(paramMap,list);
	    }
	    catch(Exception e){
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
		
		return null;
	}
	
	
	@Override
	public Flavor getFlavor(Map<String,String> paramMap,String tokenId){
		
		if(null == paramMap || 0 == paramMap.size())
			return null; //TODO throw exception
		String flavorId = paramMap.get(ParamConstant.ID);
		if(null == flavorId || flavorId.isEmpty())
			return null; //TODO throw exception
		//Firstly get the flavor from local db
		//If not, get it from OpenStack
		
		//todo 1: 通过guitokenid 取得实际，用户信息
        //AuthService	as = new AuthServiceImpl();	
        //as.GetTokenOS(guiTokenId);
		
		//TODO first, get the list from local db
		TokenOs ot = super.osToken;
	    //token should have Regioninfo
		
		String region ="RegionOne";//we should get the regioninfo by the guiTokenId
		
		String url=ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();		
		url=RequestUrlHelper.createFullUrl(url+"/flavors/"+flavorId, null);
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
	
		Map<String, String>  rs = httpClient.httpDoGet(url, headers);

		System.out.println("httpcode:"+rs.get("httpcode")); 
		System.out.println("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) != NORMAL_RESPONSE_CODE){
			System.out.println("wo cha:request failed"); 
			return null;
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
			JsonNode flavorNode=rootNode.path(ResponseConstant.FLAVOR);
            if(null == flavorNode)
            	return null; //TODO 
            
            Flavor flavor = new Flavor();
            flavor.setId(flavorNode.path(ResponseConstant.ID).textValue());
            flavor.setName(flavorNode.path(ResponseConstant.NAME).textValue());
            flavor.setVcpus(flavorNode.path(ResponseConstant.VCPUS).intValue());
            flavor.setRam(flavorNode.path(ResponseConstant.RAM).intValue());
             
            return flavor;
	    }
	    catch(Exception e){
		// TODO Auto-generated catch block
		e.printStackTrace();
	  }
		
		return null;
	}
	
	@Override
	public Flavor createFlavor(Map<String,String> paramMap,String tokenId){
		//todo 1: 通过guitokenid 取得实际，用户信息
        //AuthService	as = new AuthServiceImpl();	
        //as.GetTokenOS(guiTokenId);
		
		//TODO first, get the list from local db
		TokenOs ot = super.osToken;
	    //token should have Regioninfo
		
		String region ="RegionOne";//we should get the regioninfo by the guiTokenId
		
		String url=ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();		
		url=RequestUrlHelper.createFullUrl(url+"/flavors", null);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
		
	
		String flavorBody = generateBody(paramMap);
		Map<String, String>  rs = httpClient.httpDoPost(url, headers,flavorBody);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		System.out.println("httpcode:"+rs.get("httpcode")); 
		System.out.println("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) != NORMAL_RESPONSE_CODE){
			System.out.println("wo cha:request failed"); 
			return null;
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
			JsonNode flavorNode = rootNode.path(ResponseConstant.FLAVOR);
            if(null == flavorNode)
            	return null;
            Flavor flavor = new Flavor();
            flavor.setId(flavorNode.path(ResponseConstant.ID).textValue());
            flavor.setName(flavorNode.path(ResponseConstant.NAME).textValue());
			flavor.setDisk(flavorNode.path(ResponseConstant.DISK).intValue());
			flavor.setRam(flavorNode.path(ResponseConstant.RAM).intValue());
			flavor.setVcpus(flavorNode.path(ResponseConstant.VCPUS).intValue());
			return flavor;
			
	   }catch(Exception e){
		  // TODO Auto-generated catch block
		  e.printStackTrace();
	   }
		return null;
	}
	
	
	private String generateBody(Map<String,String> paramMap){
		if(null == paramMap || 0 == paramMap.size())
			return "";
		    
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
       
        Flavor flavor = new Flavor();
        if(null != paramMap.get(ParamConstant.NAME)){
        	flavor.setName(paramMap.get(ParamConstant.NAME));
        }
        
        if(null != paramMap.get(ParamConstant.RAM)){
        	flavor.setRam(Integer.parseInt(paramMap.get(ParamConstant.RAM)));
        }
        
        if(null != paramMap.get(ParamConstant.VCPUS)){
        	flavor.setVcpus(Integer.parseInt(paramMap.get(ParamConstant.VCPUS)));
        }
        
        if(null != paramMap.get(ParamConstant.DISK)){
        	flavor.setDisk(Integer.parseInt(paramMap.get(ParamConstant.DISK)));
        }
        
        if(null != paramMap.get(ParamConstant.ID)){
        	flavor.setId(paramMap.get(ParamConstant.ID));
        }
        
        if(null != paramMap.get(ParamConstant.SWAP)){
        	flavor.setSwap(Integer.parseInt(paramMap.get(ParamConstant.SWAP)));
        }
        
        if(null != paramMap.get(ParamConstant.RXTX_FACTOR)){
        	flavor.setRxtx_factor(paramMap.get(ParamConstant.RXTX_FACTOR));
        }
        FlavorJSON flavorJSON = new FlavorJSON(flavor);
        String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(flavorJSON);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonStr; 
	}
	
    private List<Flavor> filterFlavor(Map<String,String> paramMap,List<Flavor> flavors){
		if(null == paramMap || 0 == paramMap.size())
			return flavors;

		String flavorName = paramMap.get(ParamConstant.NAME);
		if(null != flavorName && !"".equals(flavorName)){
		    for(Flavor flavor:flavors){  
		        if(flavorName.equals(flavor.getName())){
		        	List<Flavor> goodFlavors= new ArrayList<Flavor>();	
		        	goodFlavors.add(flavor);
		        	return goodFlavors;
		        }
		    } 
		}
		
		String strLimit = paramMap.get(ParamConstant.LIMIT);
		if(null != strLimit && !"".equals(strLimit)){
			try{
				int limit = Integer.parseInt(strLimit);
				if(limit >= flavors.size())
					return flavors;
				return flavors.subList(0, limit);
			}catch(Exception e){
				//TODO
				return flavors;
			}
		}
	
		
    	return flavors;
	}
}
