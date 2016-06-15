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

import com.fnst.cloudapi.pojo.openstackapi.forgui.Flavor;
import com.fnst.cloudapi.service.openstackapi.FlavorService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.ParamConstant;


@RestController
public class FlavorController {
	@RequestMapping(value = "/flavors", method = RequestMethod.GET)
	public List<Flavor> getFlavorssList(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value=ParamConstant.LIMIT,defaultValue="") String limit,
			@RequestParam(value = ParamConstant.NAME, defaultValue = "") String name) {
		
		Map<String, String> paramMap = null;

		if (!"".equals(limit)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.LIMIT, limit);
		}
		
		if (!"".equals(name)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.NAME, name);
		}

		
		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
		// request

		// @TODO 2. guitoken should be checked, timeout or not

		FlavorService flavorService = OsApiServiceFactory.getFlavorService();
		List<Flavor> list = flavorService.getFlavorList(paramMap, guiToken);

		return list;

	}
	
	@RequestMapping(value = "/flavors", method = RequestMethod.POST)
	public Flavor createFlavor(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.NAME, defaultValue = "") String name) {
		
		Map<String, String> paramMap = null;


		if (!"".equals(name)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.NAME, name);
		}

		
		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
		// request

		// @TODO 2. guitoken should be checked, timeout or not

		FlavorService flavorService = OsApiServiceFactory.getFlavorService();
		Flavor flavor = flavorService.createFlavor(paramMap, guiToken);

		return flavor;

	}
	
	@RequestMapping(value = "/flavors/{id}", method = RequestMethod.GET)
	public Flavor getFlavor(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id){
		
		Map<String,String> paramMap = new HashMap<String,String>();
    	paramMap.put(ParamConstant.ID, id);
    	
    	FlavorService flavorService = OsApiServiceFactory.getFlavorService();
		Flavor flavor = flavorService.getFlavor(paramMap, guiToken);

		return flavor;

	}
}
