package com.fnst.cloudapi.util.interceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.util.ParamConstant;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年6月3日 上午10:10:27 
* 拦截器：对请求进行预处理
*      1. 检查从GUI传入的Token是否有效
*/
public class TokenBasedAccessInterceptor extends HandlerInterceptorAdapter {
	
	 public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
	            Object handler) throws Exception {
		 
		    String token = request.getHeader(ParamConstant.AUTH_TOKEN);
	        System.out.println("预处理..........................."+request.getHeader(ParamConstant.AUTH_TOKEN));
	        CloudUser user = new CloudUser();
	        user.setName("admin");
	        user.setCode("lucy");
	        user.setPassword("lucy");
	       /* if(token ==null || token.isEmpty()){
	        	
		        response.setStatus(401);
	        	return false;
	        }*/
            request.setAttribute("cloudUser", user);
	        return true;
	    }

}
