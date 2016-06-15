package com.fnst.cloudapi.controller.openstackapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Host;
import com.fnst.cloudapi.service.openstackapi.HostService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class HostController {

		 @RequestMapping(value="/hosts",method=RequestMethod.GET)
		 public List<Host>  getHostList(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken, 
		    		@RequestParam(value=ParamConstant.LIMIT,defaultValue="") String limit,
		    		@RequestParam(value=ParamConstant.NAME, defaultValue="") String name){
			 
			 Map<String,String> paramMap=null; 
			 
			 if(!"".equals(limit)){
				 paramMap = new HashMap<String,String>();
				 paramMap.put(ParamConstant.LIMIT, limit);
			 }
			 
			 if(!"".equals(name)){
				 if(null == paramMap)
					 paramMap = new HashMap<String,String>();
				 paramMap.put(ParamConstant.OWNER, name);
			 }
			
			 //TODO
			 
			 HostService resService= OsApiServiceFactory.getHostService();
			 List<Host> list=resService.getHostList(paramMap, guiToken);
			 return list;
		 }
		 
		 @RequestMapping(value="/hosts/{host_name}",method=RequestMethod.GET)
		 public Host getHostDetails(@PathVariable(ParamConstant.HOST_NAME) String hostName,
				 @RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken){

		     HostService resService = OsApiServiceFactory.getHostService();
		     return resService.getHostDetails(hostName,guiToken);

		 }
}
