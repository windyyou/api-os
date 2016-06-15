package com.fnst.cloudapi.util;

import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.common.CloudUser;

public class JsonHelper<JavaObject, Key> {
	
//	public static <T> generateJsonStr(<T> object){
//		
//		
//	}
	
	public  String generateJsonBodySimple(JavaObject mode,Key keyString){
		
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        String jsonbody=null;
        HashMap<Key,JavaObject> map=new HashMap<Key,JavaObject>(1);
        map.put(keyString,mode);
		try {
			
			jsonbody = mapper.writeValueAsString(map);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return jsonbody; 
		
	}
	
	public  String generateJsonBodySimple(JavaObject mode){
		
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setSerializationInclusion(Include.NON_NULL);
        mapper.setSerializationInclusion(Include.NON_EMPTY);
        String jsonbody=null;
		try {
			jsonbody = mapper.writeValueAsString(mode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        return jsonbody; 
		
	}
	
	public String generateJsonBodyWithEmpty(JavaObject mode){
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        mapper.setSerializationInclusion(Include.NON_NULL);
  //      mapper.setSerializationInclusion(Include.NON_EMPTY);
        String jsonbody=null;
		try {
			jsonbody = mapper.writeValueAsString(mode);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return jsonbody; 	
	}
	
	public static void main(String arg[]){
		CloudUser user;
        user =new CloudUser();
        user.setUserid(UUIDHelper.buildUUIDStr());
		user.setCode("cbl001");
		user.setPassword("cbl001");
		user.setName("崔保亮");
		user.setMail("cuibl@cn.fujitsu.com");
        user.setPhone("13675106341");
        user.setCompany("cblcompany");	
        
        
        JsonHelper<CloudUser, String> help=new JsonHelper<CloudUser, String>();
        System.out.println(help.generateJsonBodySimple(user));
        System.out.println(help.generateJsonBodySimple(user,"user"));
        	
	}

}
