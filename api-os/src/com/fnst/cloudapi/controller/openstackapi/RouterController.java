package com.fnst.cloudapi.controller.openstackapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.dao.common.RouterMapper;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Router;
import com.fnst.cloudapi.service.openstackapi.RouterService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;


/**
 * router 相关操作
 * @author wangw.fnst
 *
 */
@RestController
public class RouterController {
	@Autowired
	private RouterService routerServiceImpl;
	
	@Resource
	RouterMapper routerMapper;
	
	@RequestMapping(value="/routers",method=RequestMethod.GET)
    public String getRoutersList(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken, 
    		@RequestParam(value=ParamConstant.LIMIT,defaultValue="") String limit,
    		@RequestParam(value=ParamConstant.NAME, defaultValue="") String name,
    		@RequestParam(value=ParamConstant.STATUS,defaultValue="") String status, HttpServletResponse response) {
    	
		List<Router> routersFromDB = routerMapper.selectList();
		if(null != routersFromDB && 0 != routersFromDB.size()){
			JsonHelper<List<Router>, String> jsonHelp = new JsonHelper<List<Router>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(routersFromDB);
		}
		
    	Map<String,String> paramMap=null; 
    	
    	if(!"".equals(limit)){
    		paramMap=new HashMap<String,String>();
    		paramMap.put(ParamConstant.LIMIT, limit);
    	}
    	
    	if(!"".equals(name)){		
    		if(paramMap==null) paramMap=new HashMap<String,String>();
    		paramMap.put(ParamConstant.NAME, name);
    	}
    	
    	if(!"".equals(status)){		
    		if(paramMap==null) paramMap=new HashMap<String,String>();
    		paramMap.put(ParamConstant.STATUS, status);
    	}
    	
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
   	
		try {
			List<Router> routers = routerServiceImpl.getRouterList(paramMap, guiToken,response);
			if(null == routers){
				routers = new ArrayList<Router>();
				JsonHelper<List<Router>, String> jsonHelp = new JsonHelper<List<Router>, String>();
				return jsonHelp.generateJsonBodyWithEmpty(routers);
			}
			for(Router router : routers){
				if(null != routerMapper.selectByPrimaryKey(router.getId()))
					routerMapper.updateByPrimaryKeySelective(router);
				else
					routerMapper.insertSelective(router);
			}
			JsonHelper<List<Router>, String> jsonHelp = new JsonHelper<List<Router>, String>();
			return jsonHelp.generateJsonBodySimple(routers);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0006");
			return exception.getResponseMessage();
		} 
    }
	
	@RequestMapping(value="/routers/{id}",method=RequestMethod.GET)
    public String getRouter(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken, 
    		@PathVariable String id, HttpServletResponse response) {
    	
		Router routerFromDB = routerMapper.selectByPrimaryKey(id);
		if(null != routerFromDB){
			JsonHelper<Router, String> jsonHelp = new JsonHelper<Router, String>();
			return jsonHelp.generateJsonBodyWithEmpty(routerFromDB);
		}

    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
   	
		try {
			Router router = routerServiceImpl.getRouter(id, guiToken,response);
			if(null == router){
				JsonHelper<Router, String> jsonHelp = new JsonHelper<Router, String>();
				return jsonHelp.generateJsonBodyWithEmpty(new Router());
			}
			if(null != routerMapper.selectByPrimaryKey(router.getId()))
				routerMapper.updateByPrimaryKeySelective(router);
			else
				routerMapper.insertSelective(router);
			JsonHelper<Router, String> jsonHelp = new JsonHelper<Router, String>();
			return jsonHelp.generateJsonBodyWithEmpty(router);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_ROUTER_GET_0006");
			return exception.getResponseMessage();
		} 
    }
	
    @RequestMapping(value="/routers",method=RequestMethod.POST)
    public String createRouter(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@RequestBody String createBody, HttpServletResponse response) {
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
    	
		try {
			Router router = routerServiceImpl.createRouter(createBody, guiToken,response);
			if(null == router){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_ROUTERE_CREATE_0003");
				return exception.getResponseMessage();
			}
			routerMapper.insertSelective(router);
			JsonHelper<Router, String> jsonHelp = new JsonHelper<Router, String>();
			return jsonHelp.generateJsonBodySimple(router);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_ROUTER_CREATE_0006");
			return exception.getResponseMessage();
		}     
    }

}
