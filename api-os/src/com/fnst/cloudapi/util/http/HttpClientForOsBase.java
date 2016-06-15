package com.fnst.cloudapi.util.http;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.fnst.cloudapi.pojo.common.CloudConfig;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.common.TokenOsEndPoint;
import com.fnst.cloudapi.pojo.common.TokenOsEndPointV3;
import com.fnst.cloudapi.pojo.common.TokenOsEndPoints;
import com.fnst.cloudapi.util.DateHelper;

public class HttpClientForOsBase {
	
	private  String ks_puburl=null;
	private  String ks_user=null;
	private  String ks_tenantid=null;
	private  String ks_pwd=null;
	private  String ks_domainid=null;
	private  String ks_currentRegion=null;
	
	private  HttpClientForRest client=null;

	
	public HttpClientForOsBase(String ks_puburl, String ks_user,String ks_pwd, String ks_domainid,String ks_tenantid) {

		this.ks_domainid = ks_domainid;
		this.ks_tenantid = ks_tenantid;
		this.ks_puburl =ks_puburl;
		this.ks_user = ks_user;
		this.ks_pwd = ks_pwd;
		this.client = new HttpClientForRest();
	}
	
	public HttpClientForOsBase(CloudConfig cf){
		
		this.ks_puburl = cf.getOs_authurl();
		this.ks_domainid = cf.getOs_authdomainid();
		this.ks_tenantid = cf.getOs_authtenantid();
		this.ks_user = cf.getOs_authuser();
		this.ks_pwd = cf.getOs_authpwd();
		this.ks_currentRegion=cf.getOs_defaultregion();
		this.client = new HttpClientForRest();
	}

	
    public String getTokenID(){
    	if(this.ks_puburl.indexOf("v3")>-1){
    	 return getTokenIDV3();
    	}else{
    	 return getTokenIDV2();
    	}
    }
    
    public TokenOs getToken(){
    	if(this.ks_puburl.indexOf("v3")>-1){
    	 return getTokenV3();
    	}else{
    	 return getTokenV2();
    	}
    }
    
	private TokenOs getTokenV2() {
		System.out.println("token-response-v2-token:start");
		TokenOs token = null;

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-type", "application/json; charset=utf-8");
		headers.put("Accept", "application/json");
		String jsonbody = "{\"auth\": {\"tenantId\": \"" + ks_tenantid + "\",\"passwordCredentials\": {\"username\":\""
				+ ks_user + "\",\"password\":\"" + ks_pwd + "\"}}}";
		
		CloseableHttpResponse rs = null;
		try {
			rs = client.post(ks_puburl + "tokens", headers, jsonbody);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (rs == null) {

			System.out.println("wo cha:request failed");

		} else if (rs.getStatusLine().getStatusCode() > 400) {

			System.out.println("wo cha:reponse code is unsscessfully :" + rs.getStatusLine().getStatusCode());

			System.out.println("wo cha:reponse  :" + rs.toString());

		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				JsonNode node = mapper.readTree(resData);
				token = new TokenOs();
				token.setCurrentRegion(this.ks_currentRegion);
				// lets see what type the node is
				System.out.println(node.getNodeType());
				// is it a container
				System.out.println(node.isContainerNode());
				System.out.println(node.toString());
				JsonNode tokenid = node.path("access").path("token").path("id");
				token.setTokenid(tokenid.textValue());
				String ctimestr=node.path("access").path("token").path("issued_at").textValue();
				String etimestr=node.path("access").path("token").path("expires").textValue();
				String tenantid=node.path("access").path("token").path("tenant").path("id").textValue();
				
				token.setCreatetime(DateHelper.getDateByString(ctimestr));
				token.setExpirestime(DateHelper.getDateByString(etimestr));
				token.setTenantid(tenantid);

				System.out.println("tokenid.textValue():" + token);
				// test arrays
				JsonNode ss = node.findPath("serviceCatalog");
				System.out.println("serviceCatalog-isArray:"+ss.isArray());
				System.out.println("serviceCatalog-size:"+ss.size());
				ArrayList<TokenOsEndPoints> epslist= new ArrayList<TokenOsEndPoints>();
				
				for(int i=0,l=ss.size();i<l;i++){
					TokenOsEndPoints endpoints= new TokenOsEndPoints();
					endpoints.setServiceType(ss.get(i).path("type").textValue());
					endpoints.setServiceName(ss.get(i).path("name").textValue());
					System.out.println("token-response-v2-token-endpoints-i:"+i+"--"+endpoints.getServiceName());
					JsonNode jsonedps =ss.get(i).path("endpoints");
					ArrayList<TokenOsEndPoint> eplist= new ArrayList<TokenOsEndPoint>();
					
					for(int ii=0,ll=jsonedps.size();ii<ll;ii++){
						TokenOsEndPoint ep= new TokenOsEndPoint();
						ep.setRegion(jsonedps.get(ii).path("region").textValue());
						ep.setAdminURL(jsonedps.get(ii).path("adminURL").textValue());
						ep.setInternalURL(jsonedps.get(ii).path("internalURL").textValue());
						ep.setPublicURL(jsonedps.get(ii).path("publicURL").textValue());
						System.out.println("token-response-v2-token-ep-admin-ii:"+ii+"--"+ep.getAdminURL());
						eplist.add(ep);
					}
					
					endpoints.setEndpointList(eplist);
					epslist.add(endpoints);
				}
				
				token.setEndpointlist(epslist);

			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("token-response-v2-token:end");
		return token;
	}
    
    private TokenOs getTokenV3(){
		System.out.println("token-response-v3-token:start");
		TokenOs token = null;

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-type", "application/json; charset=utf-8");
		headers.put("Accept", "application/json");
		String jsonbody = "{\"auth\": {\"identity\": {\"methods\": [\"password\"],\"password\": {\"user\": {\"name\": \""
				+ ks_user + "\",\"domain\": {\"id\": \"" + ks_domainid + "\"},\"password\": \"" + ks_pwd + "\"}}},\"scope\": {\"project\": {\"id\": \""+ks_tenantid+"\"}}}}";
		
		System.out.println("jsonbody================="+jsonbody);
		CloseableHttpResponse rs = null;
	
		try {
			rs = client.post(ks_puburl + "auth/tokens", headers, jsonbody);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (rs == null) {

			System.out.println("wo cha:request failed"); // prints true

		} else if (rs.getStatusLine().getStatusCode() > 400) {

			System.out.println("wo cha:reponse code is not 200 :" + rs.getStatusLine().getStatusCode()); // prints
																											// true
			System.out.println("wo cha:reponse  :" + rs.toString()); // prints
																		// true
		} else {

			try {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				JsonNode node = mapper.readTree(resData);
				Header tokenheader = rs.getFirstHeader("X-Subject-Token");
				token= new TokenOs();
				token.setCurrentRegion(this.ks_currentRegion);
				token.setTokenid(tokenheader.getValue());

				String ctimestr=node.path("token").path("issued_at").textValue();
				String etimestr=node.path("token").path("expires_at").textValue();
				String tenantid=node.path("token").path("project").path("id").textValue();	
				String domainid=node.path("token").path("project").path("domain").path("id").textValue();
				token.setCreatetime(DateHelper.getDateByString(ctimestr));
				token.setExpirestime(DateHelper.getDateByString(etimestr));
				token.setTenantid(tenantid);
				token.setDomainid(domainid);
				
				JsonNode ss = node.findPath("catalog");
				System.out.println("Catalog:"+ss.toString());
				System.out.println("Catalog-isArray:"+ss.isArray());
				System.out.println("Catalog-size:"+ss.size());
				
				ArrayList<TokenOsEndPoints> epslist= new ArrayList<TokenOsEndPoints>();
				
				for(int i=0,l=ss.size();i<l;i++){
					TokenOsEndPoints endpoints= new TokenOsEndPoints();
					endpoints.setServiceType(ss.get(i).path("type").textValue());
					endpoints.setServiceName(ss.get(i).path("name").textValue());
					System.out.println("token-response-v3-token-i:"+i+"-"+endpoints.getServiceType());
					ArrayList<TokenOsEndPointV3> eplistv3= new ArrayList<TokenOsEndPointV3>();
					JsonNode jsonedps =ss.get(i).path("endpoints");
					
					for(int ii=0,ll=jsonedps.size();ii<ll;ii++){
						TokenOsEndPointV3 epv3= new TokenOsEndPointV3();
						epv3.setRegion(jsonedps.get(ii).path("region").textValue());
						epv3.setUrlType(jsonedps.get(ii).path("interface").textValue());
						epv3.setUrl(jsonedps.get(ii).path("url").textValue());
						System.out.println("token-response-v3-token-epv3-ii:"+ii+"-"+epv3.getUrlType()+"-"+epv3.getUrl());
						eplistv3.add(epv3);
					}
					endpoints.setEndpointListv3(eplistv3);
					epslist.add(endpoints);
				}
				
				token.setEndpointlist(epslist);
						
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("token-response-v3-token:end");
		return token;
	}

	private String getTokenIDV2(){
     System.out.println("token-response-v2:start");	 
	 String token=null;
	 
	 HashMap<String,String> headers= new HashMap<String,String>();
	 headers.put("Content-type","application/json; charset=utf-8");
	 headers.put("Accept", "application/json"); 
	 String jsonbody="{\"auth\": {\"tenantId\": \""+ks_tenantid+"\",\"passwordCredentials\": {\"username\":\""+ks_user+"\",\"password\":\""+ks_pwd+"\"}}}";
	 CloseableHttpResponse rs=null;
		try {
			rs = client.post(ks_puburl + "tokens", headers, jsonbody);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (rs == null) {

			System.out.println("wo cha:request failed"); 

		} else if (rs.getStatusLine().getStatusCode() > 400) {

			System.out.println("wo cha:reponse code is unsscessfully :" + rs.getStatusLine().getStatusCode()); 
																												
			System.out.println("wo cha:reponse  :" + rs.toString()); 
																		

		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				JsonNode node = mapper.readTree(resData);

				// lets see what type the node is
				System.out.println(node.getNodeType()); 
				// is it a container
				System.out.println(node.isContainerNode()); 
				// lets find out what fields it has
				// Iterator<String> fieldNames = node.fieldNames();
				// while (fieldNames.hasNext()) {
				// String fieldName = fieldNames.next();
				// System.out.println(fieldName);// prints title, message,
				// // errors,
				// // total,
				// // total_pages, page, limit,
				// // dataset
				// }

				System.out.println(node.toString());

				// Iterator<Entry<String, JsonNode>> ir = node.fields();
				//
				// while (ir.hasNext()) {
				//
				// Entry<String, JsonNode> et = ir.next();
				// System.out.println(et.getKey() + "-->" + et.getValue());
				//
				// }

//				JsonNode ids = node.path("access").path("token").path("audit_ids");
//				System.out.println("audit_ids:" + ids.toString());
//				System.out.println("audit_ids:" + ids.isArray());
//				System.out.println("audit_ids:" + ids.get(0).toString());
				
				JsonNode tokenid= node.path("access").path("token").path("id");
				
// 用tostring会给结果字符传上加上双引号，结果导致token值无法使用				
//				token = tokenid.toString();
//				System.out.println("tokenid.toString():"+token);
				
				token = tokenid.textValue();
				System.out.println("tokenid.textValue():"+token);
				
				// test arrays
				JsonNode ss = node.findPath("serviceCatalog");
				System.out.println("serviceCatalog-isArray:"+ss.isArray());
				System.out.println("serviceCatalog-size:"+ss.size());
				
				for(int i=0,l=ss.size();i<l;i++){
					
//					System.out.println("ss-region:" + ss.get(i).path("endpoints").get(0).path("region").textValue());
//					System.out.println("ss-internalURL:" + ss.get(i).path("endpoints").get(0).path("internalURL").textValue());
//					System.out.println("ss-publicURL:" + ss.get(i).path("endpoints").get(0).path("publicURL").textValue());
					System.out.println("ss-adminurl:" + ss.get(i).path("endpoints").get(0).path("adminURL").textValue());
//					System.out.println("ss-type:" + ss.get(i).path("type").textValue());
					System.out.println(ss.get(i).path("type").textValue());
					System.out.println("name:"+ss.get(i).path("name").textValue());
				}
				
//				System.out.println("**********************************************");	
//				for(int i=0,l=ss.size();i<l;i++){
//					System.out.println("type:"+ss.get(i).path("type").textValue());					
//					System.out.println("name:"+ss.get(i).path("name").textValue());
//				}
							
//				List<JsonNode> eds = ss.findValues("endpoints");
//				for (JsonNode one : eds) {
//					
//					System.out.println("adminurl:" + one.get(0).path("adminURL").toString());
//					System.out.println("type:" + one.get(0).path("type").toString());
//				}

			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		System.out.println("token-response-v2:end");
		return token;
	}
	
	
	private String getTokenIDV3(){
		System.out.println("token-response-v3:start");
		String token = null;

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("Content-type", "application/json; charset=utf-8");
		headers.put("Accept", "application/json");
//		String jsonbody = "{\"auth\": {\"identity\": {\"methods\": [\"password\"],\"password\": {\"user\": {\"name\": \""
//				+ ks_user + "\",\"domain\": {\"id\": \"" + ks_domainid + "\"},\"password\": \"" + ks_pwd + "\"}}}}}";
//		
		String jsonbody = "{\"auth\": {\"identity\": {\"methods\": [\"password\"],\"password\": {\"user\": {\"name\": \""
				+ ks_user + "\",\"domain\": {\"id\": \"" + ks_domainid + "\"},\"password\": \"" + ks_pwd + "\"}}},\"scope\": {\"project\": {\"id\": \""+ks_tenantid+"\"}}}}";
		
		
		CloseableHttpResponse rs = null;
	
		try {
			rs = client.post(ks_puburl + "auth/tokens", headers, jsonbody);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (rs == null) {
			
			System.out.println("wo cha:request failed"); // prints true
			
		} else if (rs.getStatusLine().getStatusCode() >400) {

			System.out.println("wo cha:reponse code is not 200 :"+rs.getStatusLine().getStatusCode()); // prints true
			System.out.println("wo cha:reponse  :"+rs.toString()); // prints true
		} else {	

		try {
			ObjectMapper mapper = new ObjectMapper();
			String resData = EntityUtils.toString(rs.getEntity());
			JsonNode node = mapper.readTree(resData);

//			// lets see what type the node is
//			System.out.println(node.getNodeType()); // prints OBJECT
//			// is it a container
//			System.out.println(node.isContainerNode()); // prints true
//			// lets find out what fields it has
//			Iterator<String> fieldNames = node.fieldNames();
//			while (fieldNames.hasNext()) {
//				String fieldName = fieldNames.next();
//				System.out.println(fieldName);// prints title, message, errors,
//												// total,
//												// total_pages, page, limit,
//												// dataset
//			}
//
//			System.out.println(node.toString());
//			System.out.println("tokens:" + node.findValue("audit_ids").get(0).toString());
//			System.out.println("tokens:" + node.findPath("audit_ids").get(0).toString());
//			
////			Header[] rsheaders=rs.getHeaders("X-Auth-Token");
////			Header[] rsheaders=rs.getAllHeaders();
////			for (Header one :rsheaders){
////			System.out.println("header:" + one.getName()+":"+one.getValue());			
////			}
						
			Header tokenheader=rs.getFirstHeader("X-Subject-Token");
			
			token=tokenheader.getValue();
			
			// Iterator<JsonNode> albums = node.path("token").iterator();
			// while (albums.hasNext()) {
			// System.out.println(albums.next().path("issued_at").asText());
			// }

		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		}
		System.out.println("token-response-v3:end");
		return token;
	}
	
	public static void main (String arg[]){

		String ks_puburlv2 = "http://193.168.141.33:5000/v2.0/";
		String ks_puburlv3 = "http://193.168.141.33:5000/v3/";
		String ks_admin = "admin";
		String ks_tenantid = "ebe4547dc1404366b79b4f133f66224a";
		String ks_adminpwd = "elb1234";
		String ks_admindomain = "default";
		String ks_region = "RegionTwo";
		
//		String ks_puburlv2 = "http://193.160.31.45:5000/v2.0/";
//		String ks_puburlv3 = "http://193.160.31.45:5000/v3/";
//		String ks_admin = "admin";
//		String ks_tenantid = "273a4096b0114573a298f18624cc9427";
//		String ks_adminpwd = "ADMIN_PASS";
//		String ks_admindomain = "default";
//		String ks_region = "RegionOne";

//		String token = "nothing";
//		token = new HttpClientForOsBase(ks_puburlv2, ks_admin, ks_adminpwd,ks_admindomain, ks_tenantid).getTokenID();
//		System.out.println("token-response-v2:" + token);
//		token = new HttpClientForOsBase(ks_puburlv3, ks_admin, ks_adminpwd, ks_admindomain,ks_tenantid).getTokenID();
//		System.out.println("token-response-v3:" + token);
		
		System.out.println("test for object v2----------------------------------------------------");
		
		CloudConfig cf = new CloudConfig(ks_puburlv2, ks_admin, ks_adminpwd, ks_admindomain,ks_tenantid);
		TokenOs tk2=new HttpClientForOsBase(cf).getToken();		
		System.out.println(tk2.getTokenid());
		System.out.println(tk2.getCreatetime().toLocaleString());		
		System.out.println(tk2.getEndPoint(TokenOs.EP_TYPE_IMAGE,ks_region).getPublicURL());
		
//		System.out.println("test for object v3----------------------------------------------------");		
//		CloudConfig cf2 = new CloudConfig(ks_puburlv3, ks_admin, ks_adminpwd, ks_admindomain,ks_tenantid);
//		TokenOs tk2=new HttpClientForOsBase(cf2).getToken();		
//		System.out.println(tk2.getTokenid());
//		System.out.println(tk2.getCreattime().toLocaleString());	
		
//		List<TokenOsEndPoints> list =tk2.getEndpointlist();
//		
//		for( TokenOsEndPoints one : list){
//			
//			System.out.println(one.getServiceType());
//			System.out.println(one.getServiceType()+":"+one.getEndpointList().size());	
//		}
		
		Map<String,TokenOsEndPoint> map=tk2.getEndpointsMap();
		System.out.println("map-size:"+map.size());
		
		Iterator<Entry<String, TokenOsEndPoint>>ir= map.entrySet().iterator();	
		
		while(ir.hasNext()){
			Entry<String, TokenOsEndPoint> ie=ir.next();
			System.out.println(ie.getKey()+":"+ie.getValue().getRegion()+":"+ie.getValue().getAdminURL());
		}
	   
		System.out.println(tk2.getEndPoint(TokenOs.EP_TYPE_IMAGE,ks_region).getPublicURL());
	//	System.out.println(tk2.getEndPoint(TokenOs.EP_TYPE_COMPUTE,ks_region).getPublicURL());
		
		//System.out.println(new HttpClientForOsBase(cf2).getTokenID());
	}

}
