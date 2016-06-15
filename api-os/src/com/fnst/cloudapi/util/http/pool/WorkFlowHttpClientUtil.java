package com.fnst.cloudapi.util.http.pool;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.util.http.HttpClientForRest;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月13日 下午1:31:33 
* @function workflow专用的httpclient
*/
@Component
@DependsOn("commonPoolingHttpManager")
public class WorkFlowHttpClientUtil extends RestHttpClientUtil {
	
	private Logger log = LogManager.getLogger(WorkFlowHttpClientUtil.class);

/**
 * post请求
 * @param url
 * @param headers
 * @param jsonbody
 * @return
 */
	
public Map<String,String>  httpDoPost(String url,String username, String password, Map inParam){
		
		CloseableHttpResponse rs = null;
		Map<String,String>   rsmap=null;
		try {
			rs=post(url,username,password, "");
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

/**
 * http GET请求
 * @param url
 * @param username
 * @param password
 * @return
 * @throws IOException 
 * @throws ClientProtocolException 
 */
	
public Map<String,String>  httpDoGet(String url,String username, String password) {
	
	
	CloseableHttpResponse rs = null;
	Map<String, String> rsmap = null;
	try {
		rs = get(url, username, password);
	} catch (ClientProtocolException e) {
		// TODO Auto-generated catch block
		log.error(e.getMessage());
		e.printStackTrace();

	} catch (IOException e) {
		// TODO Auto-generated catch block
		log.error(e.getMessage());
		e.printStackTrace();
	}

	if (rs == null) {

		System.out.println("wo cha:request failed"); // prints true

	} else {
		rsmap = new HashMap<String, String>();
		rsmap.put("httpcode", String.valueOf(rs.getStatusLine().getStatusCode()));

		try {
			Header[] rsheaders = rs.getAllHeaders();
			for (Header one : rsheaders) {
				log.error(one.getName()+":"+one.getValue());
				rsmap.put(one.getName(), one.getValue());
			}

			if (rs.getEntity() != null) {
				ObjectMapper mapper = new ObjectMapper();
				String resData = EntityUtils.toString(rs.getEntity());
				if(null != resData && !resData.isEmpty()){
					JsonNode node = mapper.readTree(resData);
					rsmap.put("jsonbody", node.toString());
				}
			} else {
				rsmap.put("jsonbody", "noresult");
			}
		} catch (ParseException | IOException e) {
			// TODO Auto-generated catch block
			log.error("json change failed:" + e.getMessage());
			e.printStackTrace();
			rsmap.put("jsonbody", "error when json convert");
		}
	}
	return rsmap;
}

}
