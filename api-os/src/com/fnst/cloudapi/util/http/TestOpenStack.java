package com.fnst.cloudapi.util.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multiset.Entry;

public class TestOpenStack {
	
	public static Map<String,String> getImageList(){
		String ks_puburlv2 = "http://controller-kilo-z:5000/v2.0/";
		String ks_puburlv3 = "http://controller-kilo-z:5000/v3/";
		String ks_admin = "admin";
		String ks_tenant = "admin";
		String ks_adminpwd = "elb1234";
		String ks_admindomain = "default";
		
		
//		String ks_puburlv2 = "http://193.160.31.45:5000/v2.0/";
//		String ks_puburlv3 = "http://193.160.31.45:5000/v3/";
//		String ks_admin = "admin";
//		String ks_tenant = "admin";
//		String ks_adminpwd = "ADMIN_PASS";
//		String ks_admindomain = "default";
		
	
		HttpClientForOsBase v2client = new HttpClientForOsBase(ks_puburlv2, ks_admin, ks_adminpwd, ks_tenant,ks_tenant);
		String tokenv2 = v2client.getTokenID();
		System.out.println("token-response-v2:" + tokenv2);
		
		String imageurl="http://controller-kilo-z:9292/v2/images";
		
		return TestOpenStack.getImageList(v2client,imageurl,tokenv2);		
		
	}
	
	
	public static Map<String,String> getImageList(HttpClientForOsBase client2,String url,String token){
		
		HttpClientForOsRequest client = new HttpClientForOsRequest();
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", token); 
		Map<String,String> imagemap =null;
		imagemap=client.httpDoGet(url, headers);
		
		System.out.println("imagemap:"+imagemap.toString());
		
		System.out.println("httpcode:"+imagemap.get("httpcode"));
		
		System.out.println("jsonbody:"+imagemap.get("jsonbody"));
		
		
		ObjectMapper mapper = new ObjectMapper();
		try {
			JsonNode node = mapper.readTree(imagemap.get("jsonbody"));
			
			JsonNode node1 = node.get(0);
			
//			List<JsonNode> images = node.findValues("images");
//
//			int size = images.size();
//			System.out.println("size:"+size);
//			
//			for (int i = 0; i < size; i++) {
//
//				Iterator<Map.Entry<String, JsonNode>> irs = images.get(i).fields();
//				while (irs.hasNext()) {
//
//					Map.Entry<String, JsonNode> et = irs.next();
//					System.out.println(et.getKey() + ":" + et.getValue().textValue());
//				}
//			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
		return imagemap;
	}

	public static void main (String arg[]){
		String ks_puburlv2 = "http://controller-kilo-z:5000/v2.0/";
		String ks_puburlv3 = "http://controller-kilo-z:5000/v3/";
		String ks_admin = "admin";
		String ks_tenant = "ebe4547dc1404366b79b4f133f66224a";
		String ks_adminpwd = "elb1234";
		String ks_admindomain = "default";
		
		
//		String ks_puburlv2 = "http://193.160.31.45:5000/v2.0/";
//		String ks_puburlv3 = "http://193.160.31.45:5000/v3/";
//		String ks_admin = "admin";
//		String ks_tenant = "273a4096b0114573a298f18624cc9427";
//		String ks_adminpwd = "ADMIN_PASS";
//		String ks_admindomain = "default";
		
	
		HttpClientForOsBase v2client = new HttpClientForOsBase(ks_puburlv2, ks_admin, ks_adminpwd,ks_admindomain, ks_tenant);
		String tokenv2 = v2client.getTokenID();
		System.out.println("token-response-v2:" + tokenv2);
		
		String imageurl="http://193.160.31.45:9292/v2/images";
		
		TestOpenStack.getImageList(v2client,imageurl,tokenv2);
		
//		HttpClientForOS v3client = new HttpClientForOS(ks_puburlv3, ks_admin, ks_adminpwd, ks_admindomain);
//		String tokenv3 = v3client.getToken();
//		System.out.println("token-response-v3:" + tokenv3);
//		
////test for get image list
//		
//		String imageurl="http://193.160.31.45:9292/v2/images";
////		String imageurl="http://193.168.141.33:9292/v2/images";		
//		HashMap<String, String> headers = new HashMap<String, String>();
//		Map<String,String> imagemap =null;
////		headers.put("Content-type","application/json; charset=utf-8");
////		headers.put("Accept", "application/json"); 
//		headers.put("X-Auth-Token", tokenv2); 
//		imagemap=v2client.httpDoGet(imageurl, headers);
//		
////		headers.put("X-Auth-Token", tokenv3); 
////		imagemap=v3client.httpDoGet(imageurl, headers);
//		
//		System.out.println("imagemap:"+imagemap.toString());
//		
//		System.out.println("httpcode:"+imagemap.get("httpcode"));
//		
//		System.out.println("jsonbody:"+imagemap.get("jsonbody"));
		
		
		
			
	}
	
}
