package com.fnst.cloudapi.util.http.pool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.common.CloudUser;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年5月25日 下午4:04:04 
* 
*/

@Component
@DependsOn("commonPoolingHttpManager")
public class OSHttpClientUtil extends RestHttpClientUtil{
	

//http tokenid失效
private static int HTTP_UAUTHORIZED_CODE = 401;

	

	public Map<String,String>  httpDoPost(String url,Map<String,String> headers,String jsonbody){
		
		CloseableHttpResponse rs = null;
		Map<String,String>   rsmap=null;
		try {
			rs=post(url, headers, jsonbody);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (rs == null) {
			
			System.out.println("wo cha:request failed"); // prints true
			
		} else {
			rsmap =new HashMap<String,String>();
			rsmap.put("httpcode", String.valueOf(rs.getStatusLine().getStatusCode()));
			
			try {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				JsonNode node = mapper.readTree(resData);
				rsmap.put("jsonbody", node.toString());
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return rsmap;
		
	}
	
	public Map<String,String>  httpDoPost(String url,String tokenid,String jsonbody){
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", tokenid);
		
		return httpDoPost(url,headers,jsonbody);
	}
	
	public Map<String,String>  httpDoPut(String url,Map<String,String> headers,String jsonbody){
		
		CloseableHttpResponse rs = null;
		Map<String,String>   rsmap=null;
		
		try {
			rs=put(url, headers, jsonbody);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (rs == null) {
			
			System.out.println("wo cha:request failed"); // prints true
			
		} else {
			rsmap =new HashMap<String,String>();
			rsmap.put("httpcode", String.valueOf(rs.getStatusLine().getStatusCode()));
			
			try {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				JsonNode node = mapper.readTree(resData);
				rsmap.put("jsonbody", node.toString());
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return rsmap;
		
	}
	
	public Map<String,String>  httpDoPut(String url,String tokenid,String jsonbody){
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", tokenid);
		
		return httpDoPut(url,headers,jsonbody);
	}
	
	public Map<String,String>  httpDoPatch(String url,Map<String,String> headers,String jsonbody){
		
		CloseableHttpResponse rs = null;
		Map<String,String>   rsmap=null;
		try {
			rs=patch(url, headers, jsonbody);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (rs == null) {
			
			System.out.println("wo cha:request failed"); // prints true
			
		} else {
			rsmap =new HashMap<String,String>();
			rsmap.put("httpcode", String.valueOf(rs.getStatusLine().getStatusCode()));
			
			try {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				JsonNode node = mapper.readTree(resData);
				rsmap.put("jsonbody", node.toString());
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return rsmap;
		
	}
	
	public Map<String,String>  httpDoPatch(String url,String tokenid,String jsonbody){
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", tokenid);
		
		return httpDoPatch(url,headers,jsonbody);
	}
	
	public Map<String,String>  httpDoDelete(String url,Map<String,String> headers){
		
		CloseableHttpResponse rs = null;
		Map<String,String>   rsmap=null;
		try {
			rs=delete(url, headers, null);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (rs == null) {
			
			System.out.println("wo cha:request failed"); // prints true
			
		} else {
			rsmap =new HashMap<String,String>();
			rsmap.put("httpcode", String.valueOf(rs.getStatusLine().getStatusCode()));
			
			try {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				JsonNode node = mapper.readTree(resData);
				rsmap.put("jsonbody", node.toString());
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}		
		return rsmap;	
	}
	
	public Map<String,String>  httpDoDelete(String url,String tokenid){
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", tokenid);
		
		return httpDoDelete(url,headers);
	}
	
	public Map<String,String>  httpDoGet(String url,Map<String,String> headers){
		
		long start = System.currentTimeMillis();
		CloseableHttpResponse rs = null;
		Map<String,String>   rsmap=null;
		try {
			rs=get(url, headers, null);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (rs == null) {
			
			System.out.println("wo cha:request failed"); // prints true
			
		} else {
			rsmap =new HashMap<String,String>();
			rsmap.put("httpcode", String.valueOf(rs.getStatusLine().getStatusCode()));
			
			try {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				JsonNode node = mapper.readTree(resData);
				rsmap.put("jsonbody", node.toString());
			} catch (ParseException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(">>>>>>>>>>>>>>GET请求总花费时间:"+(System.currentTimeMillis()-start));
		return rsmap;	
	}
	
	public Map<String,String>  httpDoGet(String url,String tokenid){
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", tokenid);
		
		return httpDoGet(url,headers);
	}
	
	/**
	 * 取得openstack的tokenID
	 * @return
	 */
	public String getOSToken(CloudUser user){
		return "";
	}
	
	/**
	 * 根据返回的http code判断token是否过期
	 * 如果过期，重新申请token
	 * @param returnCode
	 * @return
	 */
	private boolean checkExpiredToken(int returnCode){
		
		if(returnCode == HTTP_UAUTHORIZED_CODE ){
		}
		return false;
	}

}
