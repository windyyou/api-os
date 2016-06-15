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

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.dao.common.NetworkMapper;
import com.fnst.cloudapi.dao.common.PortMapper;
import com.fnst.cloudapi.dao.common.SecurityGroupMapper;
import com.fnst.cloudapi.dao.common.SecurityGroupRuleMapper;
import com.fnst.cloudapi.dao.common.SubnetMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.json.forgui.NetworkJSON;
import com.fnst.cloudapi.json.forgui.SubnetJSON;
import com.fnst.cloudapi.pojo.common.Util;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Port;
import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroup;
import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroupRule;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;
import com.fnst.cloudapi.service.openstackapi.NetworkService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.SubnetService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class NetworkController {
	@Resource
	NetworkMapper networkMapper;

	@Resource
	SubnetMapper subnetMapper;
	
	@Resource
	SecurityGroupMapper securityGroupMapper;
	
	@Resource
	SecurityGroupRuleMapper securityGroupRuleMapper;
	
	@Resource
	PortMapper portMapper;
	/**
	 * get the network list by parameter and guitoken
	 * @param guiToken guitokenid
	 * @param limit    how many to be show
	 * @param name     the name of network
	 * @param status   the status of network
	 * @return
	 */
  
    @RequestMapping(value="/networks",method=RequestMethod.GET)
    public String  getNetworksList(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
		    @RequestParam(value=ParamConstant.LIMIT,defaultValue="") String limit,
    		@RequestParam(value=ParamConstant.NAME, defaultValue="") String name,
    		@RequestParam(value=ParamConstant.STATUS,defaultValue="") String status,HttpServletResponse response) {
    	
    	List<Network> networksFromDB = networkMapper.selectAllList();
    	if(null != networksFromDB && 0 != networksFromDB.size()){
    		JsonHelper<List<Network>, String> jsonHelp = new JsonHelper<List<Network>, String>();
			return jsonHelp.generateJsonBodySimple(networksFromDB);
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
    	NetworkService nwService = OsApiServiceFactory.getNetworkService();
		try {
			List<Network> networks = nwService.getNetworkList(paramMap, guiToken,response);
			if(null == networks){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_GET_0003");
				return exception.getResponseMessage();
			}
			List<Network> networksWithSubnet = new ArrayList<Network>();
			for(Network network : networks){
				if(null != networkMapper.selectByPrimaryKey(network.getId()))
					networkMapper.updateByPrimaryKeySelective(network);
				else
				    networkMapper.insertSelective(network);
				List<String> subnetsId = network.getSubnetsId();
				for(String subnetId : subnetsId){
					Subnet subnet = subnetMapper.selectByPrimaryKey(subnetId);
					if(null != subnet){
						network.addSubnet(subnet);
					}else{
						SubnetService snService = OsApiServiceFactory.getSubnetService();
						subnet = snService.getSubnet(subnetId, guiToken,response);
						if(null == subnet)
							continue;
						network.addSubnet(subnet);
						subnetMapper.insertSelective(subnet);
					}
				}
				networksWithSubnet.add(network);
			}
			
			JsonHelper<List<Network>, String> jsonHelp = new JsonHelper<List<Network>, String>();
			return jsonHelp.generateJsonBodySimple(networksWithSubnet);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_GET_0006");
			return exception.getResponseMessage();
		}     
    }
    
    @RequestMapping(value="/networks/{id}",method=RequestMethod.GET)
    public String  getNetwork(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@PathVariable String id, HttpServletResponse response){
    	
    	Network networkFromDB = networkMapper.selectByPrimaryKey(id);
    	if(null != networkFromDB){
    		JsonHelper<Network, String> jsonHelp = new JsonHelper<Network, String>();
			return jsonHelp.generateJsonBodySimple(networkFromDB);
    	}
    	
    	    	
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	//@TODO 2. guitoken should be checked, timeout or not
		try {
			NetworkService nwService = OsApiServiceFactory.getNetworkService();
			Network network = nwService.getNetwork(id, guiToken,response);
			if(null == network){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_GET_0003");
				return exception.getResponseMessage();
			}
			if(null != networkMapper.selectByPrimaryKey(network.getId()))
				networkMapper.updateByPrimaryKeySelective(network);
			else
			    networkMapper.insertSelective(network);
			List<String> subnetsId = network.getSubnetsId();
			for(String subnetId : subnetsId){
				Subnet subnet = subnetMapper.selectByPrimaryKey(subnetId);
				if(null != subnet){
					network.addSubnet(subnet);
				}else{
					SubnetService snService = OsApiServiceFactory.getSubnetService();
					subnet = snService.getSubnet(subnetId, guiToken,response);
					if(null == subnet)
						continue;
					network.addSubnet(subnet);
					subnetMapper.insertSelective(subnet);
				}
			}
			JsonHelper<Network, String> jsonHelp = new JsonHelper<Network, String>();
			return jsonHelp.generateJsonBodySimple(network);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_GET_0006");
			return exception.getResponseMessage();
		}     
    }
    
    private String generateNetworkBody(String name, String tenant_id,Boolean state) {

		NetworkJSON networkJSON = new NetworkJSON();
		networkJSON.setNetworkInfo(name, tenant_id,state);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(networkJSON);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}
    
    private String generateSubnetkBody(Subnet subnetInfo) {
		SubnetJSON subnetJSON = new SubnetJSON();
		subnetJSON.setSubnetInfo(subnetInfo);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(subnetJSON);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}
    
    @RequestMapping(value="/networks",method=RequestMethod.POST)
    public String  createNetwork(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@RequestBody String createBody,HttpServletResponse response) {
     	ObjectMapper mapper = new ObjectMapper();
		Network networkInfo = null;
		try {
			networkInfo = mapper.readValue(createBody, Network.class);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_CREATE_0003");
			return exception.getResponseMessage();
		}
		createBody = generateNetworkBody(networkInfo.getName(),"",networkInfo.getManaged());
    	NetworkService nwService = OsApiServiceFactory.getNetworkService();
		try {
			Network network = nwService.createNetwork(createBody, guiToken, response);
			if(null == network){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_CREATE_0003");
				return exception.getResponseMessage();
			}
			network.setCreatedAt(Util.getCurrentDate());
			networkMapper.insertSelective(network);
			
			List<Subnet> subnets = networkInfo.getSubnets();
			for(Subnet subnet : subnets){
				subnet.setNetwork_id(network.getId());
				createBody = generateSubnetkBody(subnet);
				SubnetService snService= OsApiServiceFactory.getSubnetService();
	    		Subnet createdSubnet = snService.createSubnet(createBody, guiToken,response);
	    		if(null != createdSubnet){
	    			subnetMapper.insertSelective(createdSubnet);
	    			network.addSubnet(createdSubnet);
	    		}
			}
			JsonHelper<Network, String> jsonHelp = new JsonHelper<Network, String>();
			return jsonHelp.generateJsonBodySimple(network);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_CREATE_0006");
			return exception.getResponseMessage();
		}
    }
    
    @RequestMapping(value="/security-groups",method=RequestMethod.GET)
    public String getSecurityGroupsList(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
		    @RequestParam(value=ParamConstant.LIMIT,defaultValue="") String limit,
    		@RequestParam(value=ParamConstant.NAME, defaultValue="") String name,
    		@RequestParam(value=ParamConstant.STATUS,defaultValue="") String status,HttpServletResponse response) {
    	
    	List<SecurityGroup> securityGroupsFromDB = securityGroupMapper.selectAllList();
    	if(null != securityGroupsFromDB && 0 != securityGroupsFromDB.size()){
    		JsonHelper<List<SecurityGroup>, String> jsonHelp = new JsonHelper<List<SecurityGroup>, String>();
			return jsonHelp.generateJsonBodySimple(securityGroupsFromDB);
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
    	NetworkService nwService = OsApiServiceFactory.getNetworkService();
		try {
			List<SecurityGroup> securityGroups = nwService.getSecurityGroupList(paramMap, guiToken,response);
			if(null == securityGroups){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");
				return exception.getResponseMessage();
			}
			for(SecurityGroup securityGroup : securityGroups){
				if(null != securityGroupMapper.selectByPrimaryKey(securityGroup.getId()))
					securityGroupMapper.updateByPrimaryKeySelective(securityGroup);
				else
					securityGroupMapper.insertSelective(securityGroup);
			}
			JsonHelper<List<SecurityGroup>, String> jsonHelp = new JsonHelper<List<SecurityGroup>, String>();
			return jsonHelp.generateJsonBodySimple(securityGroups);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0006");
			return exception.getResponseMessage();
		}     
    }
    
    @RequestMapping(value="/security-groups/{id}",method=RequestMethod.GET)
    public String getSecurityGroup(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
		    @PathVariable String id, HttpServletResponse response) {
    	
    	SecurityGroup securityGroupFromDB = securityGroupMapper.selectByPrimaryKey(id);
    	if(null != securityGroupFromDB){
    		JsonHelper<SecurityGroup, String> jsonHelp = new JsonHelper<SecurityGroup, String>();
			return jsonHelp.generateJsonBodyWithEmpty(securityGroupFromDB);
    	}
    	
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
    	NetworkService nwService = OsApiServiceFactory.getNetworkService();
		try {
			SecurityGroup securityGroup = nwService.getSecurityGroup(id, guiToken,response);
			if(null == securityGroup){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0003");
				return exception.getResponseMessage();
			}
			if(null != securityGroupMapper.selectByPrimaryKey(securityGroup.getId()))
				securityGroupMapper.updateByPrimaryKeySelective(securityGroup);
			else
				securityGroupMapper.insertSelective(securityGroup);
			JsonHelper<SecurityGroup, String> jsonHelp = new JsonHelper<SecurityGroup, String>();
			return jsonHelp.generateJsonBodyWithEmpty(securityGroup);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_GET_0006");
			return exception.getResponseMessage();
		}     
    }
    
    @RequestMapping(value="/security-groups",method=RequestMethod.POST)
    public String createSecurityGroup(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@RequestBody String createBody, HttpServletResponse response) {
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
    	
		try {
			NetworkService nwService = OsApiServiceFactory.getNetworkService();
			SecurityGroup securityGroup = nwService.createSecurityGroup(createBody, guiToken,response);
			if(null == securityGroup){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0003");
				return exception.getResponseMessage();
			}
			securityGroup.setCreatedAt(Util.getCurrentDate());
			securityGroupMapper.insertSelective(securityGroup);
			JsonHelper<SecurityGroup, String> jsonHelp = new JsonHelper<SecurityGroup, String>();
			return jsonHelp.generateJsonBodySimple(securityGroup);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUP_CREATE_0006");
			return exception.getResponseMessage();
		}     
    }
    

    @RequestMapping(value="/security-group-rules",method=RequestMethod.GET)
    public String getSecurityGroupRulesList(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
		    @RequestParam(value=ParamConstant.LIMIT,defaultValue="") String limit,
    		@RequestParam(value=ParamConstant.NAME, defaultValue="") String name,
    		@RequestParam(value=ParamConstant.STATUS,defaultValue="") String status,HttpServletResponse response) {
    	
    	List<SecurityGroupRule> securityGroupRulesFromDB = securityGroupRuleMapper.selectAllList();
    	if(null != securityGroupRulesFromDB && 0 != securityGroupRulesFromDB.size()){
    		JsonHelper<List<SecurityGroupRule>, String> jsonHelp = new JsonHelper<List<SecurityGroupRule>, String>();
			return jsonHelp.generateJsonBodySimple(securityGroupRulesFromDB);
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
    	NetworkService nwService = OsApiServiceFactory.getNetworkService();
		try {
			List<SecurityGroupRule> securityGroupRules = nwService.getSecurityGroupRuleList(paramMap, guiToken,response);
			if(null == securityGroupRules){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0003");
				return exception.getResponseMessage();
			}
			for(SecurityGroupRule securityGroupRule : securityGroupRules){
				if(null != securityGroupRuleMapper.selectByPrimaryKey(securityGroupRule.getId()))
					securityGroupRuleMapper.updateByPrimaryKeySelective(securityGroupRule);
				else
					securityGroupRuleMapper.insertSelective(securityGroupRule);
			}
			JsonHelper<List<SecurityGroupRule>, String> jsonHelp = new JsonHelper<List<SecurityGroupRule>, String>();
			return jsonHelp.generateJsonBodySimple(securityGroupRules);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_GET_0006");
			return exception.getResponseMessage();
		}     
    }
    
    @RequestMapping(value="/security-group-rules",method=RequestMethod.POST)
    public String createSecurityGroupRule(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@RequestBody String createBody, HttpServletResponse response) {
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
    	
		try {
			NetworkService nwService = OsApiServiceFactory.getNetworkService();
			SecurityGroupRule securityGroupRule = nwService.createSecurityGroupRule(createBody, guiToken,response);
			if(null == securityGroupRule){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0003");
				return exception.getResponseMessage();
			}
			securityGroupRuleMapper.insertSelective(securityGroupRule);
			JsonHelper<SecurityGroupRule, String> jsonHelp = new JsonHelper<SecurityGroupRule, String>();
			return jsonHelp.generateJsonBodySimple(securityGroupRule);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_SECURITYGROUPRULE_CREATE_0006");
			return exception.getResponseMessage();
		}     
    }

    
    @RequestMapping(value="/ports",method=RequestMethod.GET)
    public String  getPorts(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    	    @RequestParam(value=ParamConstant.LIMIT,defaultValue="") String limit,
    		@RequestParam(value=ParamConstant.NAME, defaultValue="") String name,
    		@RequestParam(value=ParamConstant.STATUS,defaultValue="") String status,HttpServletResponse response) {
    	
    	List<Port> portsFromDB = portMapper.selectAllList();
    	if(null != portsFromDB && 0 != portsFromDB.size()){
    		List<Port> portsFromDBWithSubnet = new ArrayList<Port>();
    		for(Port port : portsFromDB){
    			try {
					setPortSubnetInfo(port,guiToken,response);
				} catch (BusinessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			portsFromDBWithSubnet.add(port);
    		}
    		JsonHelper<List<Port>, String> jsonHelp = new JsonHelper<List<Port>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(portsFromDBWithSubnet);
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
    	
    	NetworkService nwService = OsApiServiceFactory.getNetworkService();
		try {
			List<Port> ports = nwService.getPortList(paramMap, guiToken, response);
			List<Port> portsWithSubnet = new ArrayList<Port>();
    		for(Port port : ports){
    			try {
					setPortSubnetInfo(port,guiToken,response);
				} catch (BusinessException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
    			portsWithSubnet.add(port);
    		}
			JsonHelper<List<Port>, String> jsonHelp = new JsonHelper<List<Port>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(portsWithSubnet);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_PORT_GET_0006");
			return exception.getResponseMessage();
		}
    }
    

    @RequestMapping(value="/ports/{id}",method=RequestMethod.GET)
    public String  getPort(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		 @PathVariable String id, HttpServletResponse response) {
    	
    	Port portFromDB = portMapper.selectByPrimaryKey(id);
    	if(null != portFromDB){
    		try {
				setPortSubnetInfo(portFromDB,guiToken,response);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    		JsonHelper<Port, String> jsonHelp = new JsonHelper<Port, String>();
			return jsonHelp.generateJsonBodyWithEmpty(portFromDB);
    	}
    	Map<String,String> paramMap=new HashMap<String,String>();
    	paramMap.put(ParamConstant.ID, id);

    	NetworkService nwService = OsApiServiceFactory.getNetworkService();
		try {
			Port port = nwService.getPort(paramMap, guiToken, response);
	    	if(null != port){
	    		setPortSubnetInfo(port,guiToken,response);
	    		JsonHelper<Port, String> jsonHelp = new JsonHelper<Port, String>();
				return jsonHelp.generateJsonBodyWithEmpty(port);
	    	}
			JsonHelper<Port, String> jsonHelp = new JsonHelper<Port, String>();
			return jsonHelp.generateJsonBodySimple(port);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_PORT_GET_0006");
			return exception.getResponseMessage();
		}
    }
    
    @RequestMapping(value="/ports",method=RequestMethod.POST)
    public String  createPort(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@RequestBody String createBody, HttpServletResponse response) {
    	if(null == createBody){
    		ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_PORT_CREATE_0003");
    		return exception.getMessage();
    	}
    	NetworkService nwService = OsApiServiceFactory.getNetworkService();
		try {
			Port port = nwService.createPort(createBody, guiToken, response);
			port.setCreatedAt(Util.getCurrentDate());
			//update network's portid info
			Network network = networkMapper.selectByPrimaryKey(port.getNetworkId());
			if(null == network){
				network = nwService.getNetwork(port.getNetworkId(), guiToken, response);
			}
			if(null != network){
				String portId = Util.getIdWithAppendId(port.getId(),network.getPortId());
				network.setPortId(portId);
				networkMapper.updateByPrimaryKeySelective(network);	
			}
			JsonHelper<Port, String> jsonHelp = new JsonHelper<Port, String>();
			return jsonHelp.generateJsonBodySimple(port);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_PORT_CREATE_0006");
			return exception.getResponseMessage();
		}
    }
    
    private void setPortSubnetInfo(Port port,String tokenId,HttpServletResponse response) throws BusinessException{
   		Subnet subnet = subnetMapper.selectByPrimaryKey(port.getSubnetId());
		if(null != subnet){
			port.setSubnet(subnet);
		}else{
			SubnetService snService = OsApiServiceFactory.getSubnetService();
			subnet = snService.getSubnet(port.getSubnetId(), tokenId,response);
			if(null != subnet){
				subnetMapper.insertSelective(subnet);
				port.setSubnet(subnet);
			}
		}
    }
}
