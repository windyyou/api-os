package com.fnst.cloudapi.controller.openstackapi;

import java.util.List;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.pojo.openstackapi.forgui.HardQuota;
import com.fnst.cloudapi.pojo.openstackapi.forgui.StorageQuota;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.QuotaService;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class QuotaController {
	 @RequestMapping(value="/hard-quotas",method=RequestMethod.GET)
	 public List<HardQuota> getQuotas(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken){
		 
		 QuotaService resService= OsApiServiceFactory.getQuotaService();
		 List<HardQuota> list=resService.getHardQuotas(null, guiToken);
		 return list;
	 }
	 
	 @RequestMapping(value="/storage-quotas",method=RequestMethod.GET)
	 public List<StorageQuota> getStorageQuotas(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken){
		 
		 QuotaService resService= OsApiServiceFactory.getQuotaService();
		 List<StorageQuota> list=resService.getStorageQuotas(null, guiToken);
		 return list;
	 }
	 
	 @RequestMapping(value="/update-hard-quota",method=RequestMethod.POST)
	 public Boolean setHardQuota(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken){
		 
		 QuotaService resService= OsApiServiceFactory.getQuotaService();
		 return resService.setHardQuota(null, guiToken);
	 }
}
