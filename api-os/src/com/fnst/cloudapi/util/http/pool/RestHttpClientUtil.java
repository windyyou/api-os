package com.fnst.cloudapi.util.http.pool;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;


import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpTrace;
import org.apache.http.entity.StringEntity;
import org.springframework.beans.factory.annotation.Autowired;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年5月25日 下午3:07:34 
* 
*/


public class RestHttpClientUtil {
	@Autowired
	IPoolingHttpManager commonPoolingHttpManager;
	
	//http timeout
	static final int timeOut = 10 * 1000;
	
		
		public static final String HTTP_POST="POST";
		public static final String HTTP_GET="GET";
		public static final String HTTP_PUT="PUT";
		public static final String HTTP_DELETE="DELETE";
		public static final String HTTP_HEAD="HEAD";
		public static final String HTTP_TTACE="TRACE";
		public static final String HTTP_PATCH="PATCH";
		
		private CloseableHttpResponse sendRequest(HttpClient client,String url,String method,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException {  
			
			  CloseableHttpResponse response=null;
			  headers.put("Content-type","application/json; charset=utf-8");
			  headers.put("Accept", "application/json"); 
			  
			  if (HTTP_PUT.equals(method) ||HTTP_POST.equals(method)){
				  
				  response =put_post_patch(client,url,method,headers,jsonbody);
			  }else{
				  response =delete_get_trace(client,url,method,headers,jsonbody);
			  }

	         return response;  
		}
		
		private CloseableHttpResponse put_post_patch(HttpClient httpclient,String url,String method,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException {
			  
			  CloseableHttpResponse response=null;
			  HttpEntityEnclosingRequestBase httpmethod=null;
			  
			  if(HTTP_PUT.equals(method)){
				  httpmethod =new HttpPut(url);

			  }else if(HTTP_POST.equals(method)){
				  httpmethod =new HttpPost(url);
			  }else{
				  httpmethod =new HttpPatch(url);
			  }
			  
//			  httpmethod.addHeader("Content-type","application/json; charset=utf-8");  
//			  httpmethod.setHeader("Accept", "application/json");  
			  
			  for (Map.Entry<String, String> entry : headers.entrySet()) {  			  
//				  httpmethod.addHeader(entry.getKey(),entry.getValue());  
				  //@todo check
				  httpmethod.setHeader(entry.getKey(),entry.getValue());  
				}  
			  
	          StringEntity entity = new StringEntity(jsonbody,"utf-8");//解决中文乱码问题  
	          entity.setContentEncoding("UTF-8");    
	          entity.setContentType("application/json");    
			  httpmethod.setEntity(entity); 
			  
			try {

				long startTime = System.currentTimeMillis();
				System.out.println("executing request " + httpmethod.getURI());
				
				Header[] rqheaders = httpmethod.getAllHeaders();
				for (Header one : rqheaders) {
					System.out.println("header-rq:" + one.getName() + ":" + one.getValue());
				}
				System.out.println("header-rq-jsonbody:"+jsonbody.toString());
				System.out.println("header-rq-body:"+entity.toString());
				
				response = (CloseableHttpResponse) httpclient.execute(httpmethod);

				Header[] rsheaders = response.getAllHeaders();
				for (Header one : rsheaders) {
					System.out.println("header-rs:" + one.getName() + ":" + one.getValue());
				}

				long endTime = System.currentTimeMillis();
				int statusCode = response.getStatusLine().getStatusCode();
				if (statusCode != HttpStatus.SC_OK) {

				}

			} catch (IOException e) {  

	          } finally {  

	          }  

	          return 	response;	
		}
		
		
		private CloseableHttpResponse delete_get_trace(HttpClient httpclient,String url,String method,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException {  
			
			  CloseableHttpResponse response=null;
			  HttpRequestBase httpmethod=null;
			  
			  if(HTTP_GET.equals(method)){
				  httpmethod =new HttpGet(url);
			  }else if(HTTP_DELETE.equals(method)){
				  httpmethod =new HttpDelete(url);
			  }else{
				  httpmethod =new HttpTrace(url);
			  }
			  
			  for (Map.Entry<String, String> entry : headers.entrySet()) {  			  
//				  httpmethod.addHeader(entry.getKey(),entry.getValue());  
				  //@todo check
				  httpmethod.setHeader(entry.getKey(),entry.getValue());  
				}    
			  
			  try {  
				    
	            long startTime = System.currentTimeMillis();  
	            System.out.println("executing request " + httpmethod.getURI());  
	              
				Header[] rqheaders = httpmethod.getAllHeaders();
				for (Header one : rqheaders) {
					System.out.println("header-rq:" + one.getName() + ":" + one.getValue());
				}
				response = (CloseableHttpResponse) httpclient.execute(httpmethod);

				Header[] rsheaders = response.getAllHeaders();
				for (Header one : rsheaders) {
					System.out.println("header-rs:" + one.getName() + ":" + one.getValue());
				}
	            
	            long  endTime = System.currentTimeMillis();  
	            int statusCode = response.getStatusLine().getStatusCode();  
	              
//	            logger.info("statusCode:" + statusCode);  
//	            logger.info("调用API 花费时间(单位：毫秒)：" + (endTime - startTime));  
	            if (statusCode != HttpStatus.SC_OK) {  
//	                logger.error("Method failed:" + response.getStatusLine());  
//	                status = 1;  
	            }  
	  
	        } catch (IOException e) {  
	//
	        } finally {  
//	               logger.info("调用接口状态：" + status);  
	        }  

	        return 	response;			
				
		}
		
		public CloseableHttpResponse post(String url,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException{
			
			return sendRequest(commonPoolingHttpManager.getClient(),url,HTTP_POST,headers,jsonbody);
		}
		
		public CloseableHttpResponse get(String url,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException{
			
			return sendRequest(commonPoolingHttpManager.getClient(),url,HTTP_GET,headers,jsonbody);
		}
		
		public CloseableHttpResponse put(String url,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException{
			
			return sendRequest(commonPoolingHttpManager.getClient(),url,HTTP_PUT,headers,jsonbody);
		}
		
		public CloseableHttpResponse delete(String url,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException{
			
			return sendRequest(commonPoolingHttpManager.getClient(),url,HTTP_DELETE,headers,jsonbody);
		}
		
		public CloseableHttpResponse patch(String url,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException{
			
			return sendRequest(commonPoolingHttpManager.getClient(),url,HTTP_PATCH,headers,jsonbody);
		}
		
		public CloseableHttpResponse trace(String url,Map<String,String> headers,String jsonbody) throws ClientProtocolException, IOException{
			
			return sendRequest(commonPoolingHttpManager.getClient(),url,HTTP_TTACE,headers,jsonbody);
		}
		
		
		/**
		 * 带有用户名密码的POST请求
		 * @param url
		 * @param jsonbody
		 * @return
		 * @throws ClientProtocolException
		 * @throws IOException
		 */
       public CloseableHttpResponse post(String url,String username, String password,String inParam) throws ClientProtocolException, IOException{
			URL connectionURL = new URL(url);
			HttpClient client = commonPoolingHttpManager.getClientWithCredential(connectionURL.getHost(),connectionURL.getPort(),username,password);
			Map header =  new HashMap();
			return sendRequest(client,url,HTTP_POST,header,inParam);
		}
       /**
        * 带有用户名密码的GET请求
        * @param url
        * @return
        * @throws ClientProtocolException
        * @throws IOException
        */
       public CloseableHttpResponse get(String url,String username, String password) throws ClientProtocolException, IOException{
    	   URL connectionURL = new URL(url);
    	   HttpClient client = commonPoolingHttpManager.getClientWithCredential(connectionURL.getHost(),connectionURL.getPort(),username,password);
    	   Map header =  new HashMap();
    	   return sendRequest(client,url,HTTP_GET,header,null);
       }
		
	}


