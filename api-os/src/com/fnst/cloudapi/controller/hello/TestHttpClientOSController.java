package com.fnst.cloudapi.controller.hello;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.util.http.HttpClientForOsBase;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.TestOpenStack;



@RestController
public class TestHttpClientOSController {
	
	
	@RequestMapping(value="/tokenv2",method=RequestMethod.GET)
	String gettokenv2(){
		String ks_puburlv2 = "http://193.168.141.33:5000/v2.0/";
		String ks_puburlv3 = "http://193.168.141.33:5000/v3/";
		String ks_admin = "admin";
		String ks_tenant = "ebe4547dc1404366b79b4f133f66224a";
		String ks_adminpwd = "elb1234";
		String ks_admindomain = "default";

		String token = "nothing";
		token = new HttpClientForOsBase(ks_puburlv2, ks_admin, ks_adminpwd, ks_tenant,ks_tenant).getTokenID();
		System.out.println("token-response-v2:" + token);
//		token = new HttpClientForOS(ks_puburlv3, ks_admin, ks_adminpwd, ks_admindomain).getToken();
//		System.out.println("token-response-v3:" + token);
		
		return token;
	}
	
	@RequestMapping(value="/tokenv3",method=RequestMethod.GET)
	String gettokenv3(){
		
		String ks_puburlv2 = "http://193.168.141.33:5000/v2.0/";
		String ks_puburlv3 = "http://193.168.141.33:5000/v3/";
		String ks_admin = "admin";
		String ks_tenant = "ebe4547dc1404366b79b4f133f66224a";
		String ks_adminpwd = "elb1234";
		String ks_admindomain = "default";

		String token = "nothing";
//		token = new HttpClientForOS(ks_puburlv2, ks_admin, ks_adminpwd, ks_tenant).getToken();
//		System.out.println("token-response-v2:" + token);
		token = new HttpClientForOsBase(ks_puburlv3, ks_admin, ks_adminpwd, ks_admindomain,ks_tenant).getTokenID();
		System.out.println("token-response-v3:" + token);
		
		return token;
	}
	
	@RequestMapping(value="/imagescbl",method=RequestMethod.GET)
	Map<String,String> imagescbl(){
		
		return TestOpenStack.getImageList();
		
	}
	
//	@RequestMapping(value="/tokenv2",method=RequestMethod.GET)
//	List<? extends User> getUsers(){
//		
//		return TestOS4J.getUsers();
//	}
//	
//	@RequestMapping(value="/tokenv3",method=RequestMethod.GET)
//	List<? extends Image> getImages(){
//		
//		return TestOS4J.getImages();
//	}
	
//	@RequestMapping(value="/tenants",method=RequestMethod.GET)
//	List<? extends Tenant> getTenants(){
//		
//		return TestOS4J.getTenants();
//	}
//	
//	@RequestMapping(value="/tenant?name=&ddds=sd&dsdfsdfs=dddd",method=RequestMethod.POST)
//	Tenant createTenant(@RequestParam(value="name") String name,@RequestParam(value="description") String desc){
//	
//		return TestOS4J.createTenantv2(name,desc);
//	}
	
}
