package com.fnst.cloudapi.controller.openstackapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.dao.common.NetworkMapper;
import com.fnst.cloudapi.dao.common.SubnetMapper;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.Util;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.SubnetService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class SubnetController {
	
	@Resource
	SubnetMapper subnetMapper;
	
	@Resource
	NetworkMapper networkMapper;
	
	/**
	 * get the subnet list by parameter and guitoken
	 *  - this user's subnets filter by opentack api (tenant_id)
	 *  - network filter by openstack api
	 *  -  
	 * @param guiToken guitokenid
	 * @param limit    how many to be show
	 * @param name     the name of subnet
	 * @param status   the status of subnet 
	 * @param network   the id of network
	 * @return
	 */
  
    @RequestMapping(value="/subnets",method=RequestMethod.GET)
    public String  getSubnetsList(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
		    @RequestParam(value=ParamConstant.LIMIT,defaultValue="") String limit,
    		@RequestParam(value=ParamConstant.NAME, defaultValue="") String name,
    		@RequestParam(value=ParamConstant.NETWORK_ID,defaultValue="") String network_id,HttpServletResponse response) {
    	
     	List<Subnet> subnetsFromDB = subnetMapper.selectAllList();
    	if(null != subnetsFromDB && 0 != subnetsFromDB.size()){
    		JsonHelper<List<Subnet>, String> jsonHelp = new JsonHelper<List<Subnet>, String>();
			return jsonHelp.generateJsonBodySimple(subnetsFromDB);
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
    	
    	if(!"".equals(network_id)){		
    		if(paramMap==null) paramMap=new HashMap<String,String>();
    		paramMap.put(ParamConstant.NETWORK_ID, network_id);
    	}
    	
    	    	
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
   	
    	SubnetService snService = OsApiServiceFactory.getSubnetService();
		try {
			List<Subnet> subnets = snService.getSubnetList(paramMap, guiToken,response);
			if(null == subnets){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");
				return exception.getResponseMessage();
			}
			List<Subnet> subnetsWithNetwork = new ArrayList<Subnet>();
			for(Subnet subnet : subnets){
				if(null != subnetMapper.selectByPrimaryKey(subnet.getId()))
					subnetMapper.updateByPrimaryKeySelective(subnet);
				else
				    subnetMapper.insertSelective(subnet);
				Network network = networkMapper.selectByPrimaryKey(subnet.getNetwork_id());
				if(null != network)
					subnet.setNetwork(network);
				else{
					subnet.setNetwork(network);
				}
				
			}
			JsonHelper<List<Subnet>, String> jsonHelp = new JsonHelper<List<Subnet>, String>();
			return jsonHelp.generateJsonBodySimple(subnetsWithNetwork);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0006");
			return exception.getResponseMessage();
		}  
        
    }

    @RequestMapping(value="/subnets/{id}",method=RequestMethod.GET)
    public String  getSubnet(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@PathVariable String id, HttpServletResponse response){
    	
     	Subnet subnetFromDB = subnetMapper.selectByPrimaryKey(id);
    	if(null != subnetFromDB){
    		JsonHelper<Subnet, String> jsonHelp = new JsonHelper<Subnet, String>();
			return jsonHelp.generateJsonBodySimple(subnetFromDB);
    	}
    	 
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
		try {
			SubnetService snService = OsApiServiceFactory.getSubnetService();
			Subnet subnet = snService.getSubnet(id, guiToken,response);
			if(null == subnet){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0003");
				return exception.getResponseMessage();
			}
            if(null != subnetMapper.selectByPrimaryKey(id))
            	subnetMapper.updateByPrimaryKeySelective(subnet);
            else
				subnetMapper.insertSelective(subnet);
			JsonHelper<Subnet, String> jsonHelp = new JsonHelper<Subnet, String>();
			return jsonHelp.generateJsonBodySimple(subnet);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SUBNET_GET_0006");
			return exception.getResponseMessage();
		}  
        
    }
    
    @RequestMapping(value="/subnets",method=RequestMethod.POST)
    public String createSubnet(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@RequestBody String createBody, HttpServletResponse response){
    	
    	try {
    		SubnetService snService= OsApiServiceFactory.getSubnetService();
    		Subnet subnet = snService.createSubnet(createBody, guiToken,response);
			if(null == subnet){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0003");
				return exception.getResponseMessage();
			}
			subnet.setCreatedAt(Util.getCurrentDate());
			subnetMapper.insertSelective(subnet);
			//update network's subnetid info
			Network network = networkMapper.selectByPrimaryKey(subnet.getNetwork_id());
			if(null == network){
				//TODO
			}else{
				String subnetId = Util.getIdWithAppendId(subnet.getId(),network.getSubnetId());
				network.setSubnetId(subnetId);
				networkMapper.updateByPrimaryKeySelective(network);	
			}
			
			JsonHelper<Subnet, String> jsonHelp = new JsonHelper<Subnet, String>();
			return jsonHelp.generateJsonBodySimple(subnet);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0006");
			return exception.getResponseMessage();
		}     
    	
    }
}
