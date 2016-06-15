package com.fnst.cloudapi.controller.openstackapi;

import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Price;
import com.fnst.cloudapi.util.ParamConstant;


@RestController
public class PriceController {
	 @RequestMapping(value="/prices",method=RequestMethod.GET)
	 public List<Price> getPools(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
			     @RequestParam(value=ParamConstant.NAME,defaultValue="") String name){
		 
		 return null;
	 }
}
