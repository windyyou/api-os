package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Router;


public interface RouterService {
	
	public List<Router> getRouterList(Map<String, String> paramMap, String tokenId,HttpServletResponse response) throws BusinessException;
	public Router getRouter(String routerId, String tokenId,HttpServletResponse response) throws BusinessException;
	public Router createRouter(String createBody, String tokenId,HttpServletResponse response) throws BusinessException;
}
