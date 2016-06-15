package com.fnst.cloudapi.service.openstackapi.impl;

import java.util.List;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.foros.Role;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.RoleService;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

//@Service
public class RoleServiceImpl extends CloudConfigAndTokenHandler implements RoleService{
	private HttpClientForOsRequest httpClient=null;
    private int ERROR_HTTP_CODE = 400;
	private Logger log = LogManager.getLogger(RoleServiceImpl.class);
    
	public RoleServiceImpl() {
		httpClient = new HttpClientForOsRequest();
	}
	@Override
	public Role createRole(Role role) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean deleteRole(Role role) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean grantRoleToUserOnProject(String role_id, String user_id, String project_id) throws Exception {
		// TODO Auto-generated method stub
		
		//todo 1: 使用默认配置admin用户创建租户
		TokenOs ot = super.osToken;	

		String url=super.osConfig.getOs_authurl();
		log.debug("role_id:"+role_id); 
		log.debug("user_id:"+user_id); 
		log.debug("project_id:"+project_id); 
		StringBuffer sb= new StringBuffer();
		sb.append(url);
		sb.append("projects");
		sb.append("/");
		sb.append(project_id);
		sb.append("/");
		sb.append("users");
		sb.append("/");
		sb.append(user_id);
		sb.append("/");		
		sb.append("roles");
		sb.append("/");
		sb.append(role_id);
        url=sb.toString();
		String url1=super.osConfig.getOs_authurl()+"projects/​"+project_id.trim()+"/users/"+user_id.trim()+"/roles/​"+role_id.trim();
		//url+="projects/​"+project_id.trim()+"/users/"+user_id.trim()+"/roles/​"+role_id.trim();
		log.debug("url:"+url); 
		log.debug("url1:"+url1); 
		Map<String, String>  rs =httpClient.httpDoPut(url,ot.getTokenid(),"");
		
		if(rs==null||Integer.parseInt(rs.get("httpcode")) >= ERROR_HTTP_CODE){
			log.debug("wo cha：get rolelist request failed"); 
			return false;
		}
		
		log.debug("httpcode:"+rs.get("httpcode")); 
		log.debug("jsonbody:"+rs.get("jsonbody")); 
		
		return true;
	}


	@Override
	public List<Role> getRoleList() throws Exception {
		//todo 1: 使用默认配置admin用户创建租户
		TokenOs ot = super.osToken;

		//but keystone shuild be in default region
		//String region =OpenStackBaseConstant.OS_DEFAULT_REGION;
		
		//String url=ot.getEndPoint(TokenOs.EP_TYPE_IDENTIFY, region).getPublicURL();	
		String url=super.osConfig.getOs_authurl();
		url=RequestUrlHelper.createFullUrl(url+"/roles",null);
		
		Map<String, String>  rs =httpClient.httpDoGet(url,ot.getTokenid());
		
		log.debug("httpcode:"+rs.get("httpcode")); 
		log.debug("jsonbody:"+rs.get("jsonbody")); 
			
		if(Integer.parseInt(rs.get("httpcode")) > ERROR_HTTP_CODE){
			log.debug("wo cha：get rolelist request failed"); 
			throw new Exception("et rolelist request failed");
		}
		List<Role> ll=null;
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
			JsonNode rolesNode =rootNode.path(ResponseConstant.ROLES);
			ll = mapper.readValue(rolesNode.toString(),new TypeReference<List<Role>>(){});
		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		}
		 return ll;
	}

	@Override
	public Role getRoleByName(String name) throws Exception {
		// TODO Auto-generated method stub
		
		List<Role> rolelist=this.getRoleList();
	    if(rolelist!=null){	    	
	    	for(Role role:rolelist){
	    		if(role.getName().equals(name)) return role;
	    	}
	    }
		
		return null;
	}

}
