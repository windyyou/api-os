package com.fnst.cloudapi.controller.openstackapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Tenant;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.TenantService;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class TenantController {
	 @RequestMapping(value="/alltenants",method=RequestMethod.GET)
	 public List<Tenant>  getTenantList(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
	    		@RequestParam(value=ParamConstant.NAME, defaultValue="") String name){
		 
		 Map<String,String> paramMap=null; 
		
		 if(!"".equals(name)){
			 if(null == paramMap)
				 paramMap = new HashMap<String,String>();
			 paramMap.put(ParamConstant.NAME, name);
		 }
		
		 //TODO
		 
		 TenantService resService= OsApiServiceFactory.getTenantService();
		 List<Tenant> list=resService.getTenantList(paramMap, guiToken);
		 
		 return list;
	 }
}
