package com.fnst.cloudapi.service.openstackapi.impl;

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
import com.fnst.cloudapi.pojo.openstackapi.foros.Project;
import com.fnst.cloudapi.pojo.openstackapi.foros.User;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.UserService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;
//@Service
public class UserServiceImpl extends CloudConfigAndTokenHandler implements UserService{
	private HttpClientForOsRequest httpClient=null;
    private int ERROR_HTTP_CODE = 400;
	private Logger log = LogManager.getLogger(UserServiceImpl.class);
	
	public UserServiceImpl() {
		httpClient = new HttpClientForOsRequest();
	}	
	
	@Override
	public User createUser(User user) throws Exception {
		//todo 1: 使用默认配置admin用户创建租户
		if(user==null) throw new Exception("project is null");  
		TokenOs ot = super.osToken;
		user.setDomain_id(super.osConfig.getOs_authdomainid());	
		String url=super.osConfig.getOs_authurl();
		url=RequestUrlHelper.createFullUrl(url+"/users",null);
		
		//生成post body体
		String postbody=new JsonHelper<User,String>().generateJsonBodySimple(user, "user");
		log.debug("postbody:"+postbody); 
		
		Map<String, String>  rs =httpClient.httpDoPost(url,ot.getTokenid(), postbody);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		log.debug("httpcode:"+rs.get("httpcode")); 
		log.debug("jsonbody:"+rs.get("jsonbody")); 
			
		if(rs==null||Integer.parseInt(rs.get("httpcode")) > ERROR_HTTP_CODE){
			log.debug("wo cha:create project request failed"); 
			throw new Exception("create project request failed");
		}
		
		try {
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
			JsonNode userNode = rootNode.path(ResponseConstant.USER);	
			user = mapper.readValue(userNode.toString(), User.class);

		}catch(Exception e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return user;
	}

	@Override
	public boolean deleteUser(User user) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

}
