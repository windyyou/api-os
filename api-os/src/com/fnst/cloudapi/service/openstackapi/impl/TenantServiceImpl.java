package com.fnst.cloudapi.service.openstackapi.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Tenant;
import com.fnst.cloudapi.pojo.openstackapi.foros.Project;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.TenantService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

//@Service
public class TenantServiceImpl extends CloudConfigAndTokenHandler implements TenantService{
	private HttpClientForOsRequest httpClient=null;
    private int ERROR_HTTP_CODE = 400;
	private Logger log = LogManager.getLogger(TenantServiceImpl.class);
	public TenantServiceImpl() {
		httpClient = new HttpClientForOsRequest();
	}
	
	@Override
	public List<Tenant> getTenantList(Map<String,String> paramMap,String tokenId){
		//todo 1: 通过guitokenid 取得实际，用户信息
        //AuthService	as = new AuthServiceImpl();	
        //as.GetTokenOS(guiTokenId);
		   
		TokenOs ot = super.osToken;
	    //token should have Regioninfo
		
		
		String region ="RegionOne";//we should get the regioninfo by the guiTokenId
		
		String url=ot.getEndPoint(TokenOs.EP_TYPE_IDENTIFY, region).getPublicURL();		
		url=RequestUrlHelper.createFullUrl(url+"/tenants", paramMap);
		
				
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
		
		
		Map<String, String>  rs =httpClient.httpDoGet(url, headers);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		System.out.println("httpcode:"+rs.get("httpcode")); 
		System.out.println("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) >= ERROR_HTTP_CODE){
			System.out.println("wo cha:request failed"); 
			return null;
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
			JsonNode tenantsNode=rootNode.path(ResponseConstant.TENANTS);
			int tenantsCount =tenantsNode.size();
            if(0 == tenantsCount)
            	return null;
            
            List<Tenant> list= new ArrayList<Tenant>();	
			for(int index = 0; index < tenantsCount; ++index){
				Tenant tenant = new Tenant();
				JsonNode tenantNode = tenantsNode.get(index);
				
				tenant.setDescription(tenantNode.path(ResponseConstant.DESCRIPTION).textValue());
				tenant.setEnabled(tenantNode.path(ResponseConstant.ENABLED).booleanValue());
				tenant.setId(tenantNode.path(ResponseConstant.ID).textValue());
				tenant.setName(tenantNode.path(ResponseConstant.NAME).textValue());
				
			    list.add(tenant);
		     }
			return list;
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return null;
	}

	@Override
	public Project createProject(Project project) throws Exception {
		//step 1: 使用默认配置admin用户创建租户
		if(project==null) throw new Exception("project is null");  
		TokenOs ot = super.osToken;
		project.setDomain_id(super.osConfig.getOs_authdomainid());
		//we should get the regioninfo by the guiTokenId
		//but keystone shuild be in default region
		//String region =OpenStackBaseConstant.OS_DEFAULT_REGION;
		
		//String url=ot.getEndPoint(TokenOs.EP_TYPE_IDENTIFY, region).getPublicURL();	
		String url=super.osConfig.getOs_authurl();
		url=RequestUrlHelper.createFullUrl(url+"/projects",null);
		
		//生成post body体
		String postbody=new JsonHelper<Project,String>().generateJsonBodySimple(project, "project");
		log.debug("postbody:"+postbody); 
		
		Map<String, String>  rs =httpClient.httpDoPost(url,ot.getTokenid(), postbody);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		log.debug("httpcode:"+rs.get("httpcode")); 
		log.debug("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) >= ERROR_HTTP_CODE){
			log.debug("wo cha:create project request failed"); 
			throw new Exception("create project request failed");
		}
	
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
			JsonNode projectNode =rootNode.path(ResponseConstant.PROJECT);
			//Map<String,Project> map = mapper.readValue(rs.get("jsonbody"),  Map.class);
			log.debug("projectNode.asText():"+projectNode.asText()); 
			log.debug("projectNode.toString():"+projectNode.toString()); 
			project=mapper.readValue(projectNode.toString(),Project.class);

		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		 return project;
	}
	
}
