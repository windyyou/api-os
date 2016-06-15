package com.fnst.cloudapi.controller.common;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.dao.common.CloudUserMapper;
import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.service.common.CloudUserService;
import com.fnst.cloudapi.util.UUIDHelper;

@RestController
public class CloudUserController {
	
	@Resource
	CloudUserService cloudUserService;
	@Resource
	CloudUserMapper cloudUserMapper;	
    /**
     * for get request and json content
     * get data from json content
     * @param name
     * @return
     */
    @RequestMapping("/users")
    public String createUser(@RequestHeader(value="X-ApiAuth-Token",defaultValue="nownoimpl") String guiToken,@RequestBody String createBody,HttpServletResponse response) {
    	
    	 ObjectMapper mapper = new ObjectMapper();
    	 CloudUser cloudUser = null;
    	 boolean hasException= true;
         try {
        	 
        	 cloudUser= mapper.readValue(createBody,  CloudUser.class);
        	 hasException=false;
         } catch (JsonParseException e) {
        	 
             e.printStackTrace();
             
         } catch (JsonMappingException e) {
        	 
             e.printStackTrace();
             
         } catch (IOException e) {
        	 
             e.printStackTrace();
         }
              
        try {
        	 
			cloudUserService.insertUserAndTenant(cloudUser);
			hasException=false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         if(hasException){
             response.setStatus(502);   
             cloudUser=null;
         }
 
         return "";
    }
        
    /**
     * for get request and json content
     * get data from json content
     * @param name
     * @return
     */
    @RequestMapping("/test/users")
    public CloudUser justestCreateUser(@RequestHeader(value="X-ApiAuth-Token",defaultValue="nownoimpl") String guiToken) {
    	
//    	 ObjectMapper mapper = new ObjectMapper();
//    	 CloudUser cloudUser = null;
//         try {
//        	 
//        	 cloudUser= mapper.readValue(createBody,  CloudUser.class);
//        	 
//         } catch (JsonParseException e) {
//             e.printStackTrace();
//         } catch (JsonMappingException e) {
//             e.printStackTrace();
//         } catch (IOException e) {
//             e.printStackTrace();
//         }
    	
    	CloudUser user =new CloudUser();
        user.setUserid(UUIDHelper.buildUUIDStr());
		user.setCode("cbl001");
		user.setPassword("cbl001");
		user.setName("崔保亮");
		user.setMail("cuibl@cn.fujitsu.com");
        user.setPhone("13675106341");
        user.setCompany("cbl company");
        user.setOsdomainid("default");
        user.setOstenantid("default");
         try {
        	 cloudUserMapper.insertSelective(user);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
         
         
         return user;
    }

}
