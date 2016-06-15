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

import com.fnst.cloudapi.dao.common.FloatingIPMapper;
import com.fnst.cloudapi.dao.common.InstanceMapper;
import com.fnst.cloudapi.dao.common.NetworkMapper;
import com.fnst.cloudapi.dao.common.RouterMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.Util;
import com.fnst.cloudapi.pojo.openstackapi.forgui.FloatingIP;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Instance;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Router;
import com.fnst.cloudapi.service.openstackapi.FloatingIPService;
import com.fnst.cloudapi.service.openstackapi.InstanceService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.RouterService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

/**
 * 
 * floating ip操作
 *
 */
@RestController
public class FloatingIPController {

	@Autowired
	private RouterService routerServiceImpl;
	
	@Resource
	FloatingIPMapper floatingipMapper;
	
	@Resource
	InstanceMapper instanceMapper;
	
	@Resource
	NetworkMapper networkMapper;
	
	@Resource
	RouterMapper routerkMapper;
	
	@Autowired
	private FloatingIPService floatingIPServiceImpl;

	@RequestMapping(value = "/floating-ips", method = RequestMethod.GET)
	public String getFloatingIPList(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.STATUS, defaultValue = "") String status,
			@RequestParam(value = ParamConstant.LIMIT, defaultValue = "") String limit, HttpServletResponse response) {

		List<FloatingIP> floatingipsFromDB = floatingipMapper.selectList();
		if(null != floatingipsFromDB && 0 != floatingipsFromDB.size()){
			List<FloatingIP> floatingipsWithResource = new ArrayList<FloatingIP>();
			for(FloatingIP floatingip : floatingipsFromDB){
				try {
					addAttachedResourceInfo(floatingip,guiToken,response);
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				floatingipsWithResource.add(floatingip);
			}
			JsonHelper<List<FloatingIP>, String> jsonHelp = new JsonHelper<List<FloatingIP>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(floatingipsWithResource);
		}
		
		Map<String, String> paramMap = null;
 
		if (!"".equals(limit)) {
			paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.STATUS, status);
		}
		if (!"".equals(limit)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.LIMIT, limit);
		}

		try {
			List<FloatingIP> floatingips = floatingIPServiceImpl.getFloatingIPList(paramMap, guiToken,response);
			if(null == floatingips){
				floatingips = new ArrayList<FloatingIP>();
				JsonHelper<List<FloatingIP>, String> jsonHelp = new JsonHelper<List<FloatingIP>, String>();
				return jsonHelp.generateJsonBodyWithEmpty(floatingips);
			}
			List<FloatingIP> floatingipsWithResource = new ArrayList<FloatingIP>();
			for(FloatingIP floating : floatingips){
				try {
					addAttachedResourceInfo(floating,guiToken,response);
				} catch (BusinessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(null != floatingipMapper.selectByPrimaryKey(floating.getId()))
					floatingipMapper.updateByPrimaryKeySelective(floating);
				else
					floatingipMapper.insertSelective(floating);
				floatingipsWithResource.add(floating);
			}
			JsonHelper<List<FloatingIP>, String> jsonHelp = new JsonHelper<List<FloatingIP>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(floatingipsWithResource);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_FLOATINGIP_GET_0006");
			return exception.getResponseMessage();
		} 
	}

	private void addAttachedResourceInfo(FloatingIP floatingip,String tokenId,HttpServletResponse response) throws BusinessException{
		if(null == floatingip)
			return;
		String attchedRouterId = floatingip.getRouterId();
		if(null != attchedRouterId && !attchedRouterId.isEmpty()){
			Router router = routerkMapper.selectByPrimaryKey(attchedRouterId);
			if(null != router){
				floatingip.addResource(attchedRouterId, router.getName(), ParamConstant.ROUTER);
			}else{
				router = routerServiceImpl.getRouter(attchedRouterId, tokenId, response);
				if(null != router){
					routerkMapper.insertSelective(router);
					floatingip.addResource(attchedRouterId, router.getName(), ParamConstant.ROUTER);
				}
			}
		}
		String attachedInstanceId = floatingip.getInstanceId();
		if(null != attachedInstanceId){
			Instance instance = instanceMapper.selectByPrimaryKey(attachedInstanceId);
			if(null != instance){
				floatingip.addResource(attachedInstanceId, instance.getName(), ParamConstant.INSTANCE);
			}else{
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put(ParamConstant.ID, attachedInstanceId);
				InstanceService inService = OsApiServiceFactory.getInstanceService();
				instance = inService.showInstance(paramMap, tokenId, false, null, response);
				if(null != instance){
					instanceMapper.insertSelective(instance);
					floatingip.addResource(attchedRouterId, instance.getName(), ParamConstant.ROUTER);
				}
			}
		}
	}
	
	@RequestMapping(value = "/floating-ips/{id}", method = RequestMethod.GET)
	public String getFloatingIP(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response) {

		FloatingIP floatingipFromDB = floatingipMapper.selectByPrimaryKey(id);
		if(null != floatingipFromDB ){
			try {
				addAttachedResourceInfo(floatingipFromDB,guiToken,response);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_FLOATINGIP_GET_0003");
				return exception.getResponseMessage();
			}
			JsonHelper<FloatingIP, String> jsonHelp = new JsonHelper<FloatingIP, String>();
			return jsonHelp.generateJsonBodyWithEmpty(floatingipFromDB);
		}
		
		try {
			FloatingIP floatingip = floatingIPServiceImpl.getFloatingIP(id, guiToken,response);
			if(null == floatingip){
				JsonHelper<FloatingIP, String> jsonHelp = new JsonHelper<FloatingIP, String>();
				return jsonHelp.generateJsonBodyWithEmpty(new FloatingIP());
			}
			try {
				addAttachedResourceInfo(floatingipFromDB,guiToken,response);
			} catch (BusinessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_FLOATINGIP_GET_0003");
				return exception.getResponseMessage();
			}
			if(null != floatingipMapper.selectByPrimaryKey(floatingip.getId()))
				floatingipMapper.updateByPrimaryKeySelective(floatingip);
			else
				floatingipMapper.insertSelective(floatingip);
			JsonHelper<FloatingIP, String> jsonHelp = new JsonHelper<FloatingIP, String>();
			return jsonHelp.generateJsonBodyWithEmpty(floatingip);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_FLOATINGIP_GET_0006");
			return exception.getResponseMessage();
		} 
	}
	
    @RequestMapping(value="/floating-ips",method=RequestMethod.POST)
    public String createFloatingIp(@RequestHeader(value=ParamConstant.AUTH_TOKEN,defaultValue="nownoimpl") String guiToken,
    		@RequestBody String createBody, HttpServletResponse response) {
    	//@TODO 1. guitoken should has no defaultValue,if there no token ,bad request  
    	
    	//@TODO 2. guitoken should be checked, timeout or not
    	
		try {
			FloatingIP floatingIp = floatingIPServiceImpl.createFloatingIp(createBody, guiToken,response);
			if(null == floatingIp){
				ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_FLOATINGIP_CREATE_0003");
				return exception.getResponseMessage();
			}
			floatingIp.setCreateddAt(Util.getCurrentDate());
			floatingipMapper.insertSelective(floatingIp);
			//update network's portid info
			Network network = networkMapper.selectByPrimaryKey(floatingIp.getNetworkId());
			if(null == network){
				//TODO
			}else{
				String floatingIpId = Util.getIdWithAppendId(floatingIp.getId(),network.getFloatingipId());
				network.setFloatingipId(floatingIpId);;
				networkMapper.updateByPrimaryKeySelective(network);	
			}
			JsonHelper<FloatingIP, String> jsonHelp = new JsonHelper<FloatingIP, String>();
			return jsonHelp.generateJsonBodySimple(floatingIp);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_NETWORK_FLOATINGIP_CREATE_0006");
			return exception.getResponseMessage();
		}     
    }
//	/**
//	 * 
//	 * @param guiToken
//	 * @param limit
//	 * @param name
//	 * @param status
//	 * @param imageid
//	 * @return list<FloatingIPExt>
//	 */
//	@RequestMapping(value = "/floating-ips", method = RequestMethod.GET)
//	public List<FloatingIP> getSnapshotList(
//			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
//			@RequestParam(value = "limit", defaultValue = "") String limit,
//			@RequestParam(value = "name", defaultValue = "") String name,
//			@RequestParam(value = "status", defaultValue = "") String status) {
//
//		Map<String, String> paramMap = null;
//
//		long start = System.currentTimeMillis();
//
//		if (!"".equals(limit)) {
//			paramMap = new HashMap<String, String>();
//			paramMap.put("limit", limit);
//		}
//
//		if (!"".equals(name)) {
//			if (paramMap == null)
//				paramMap = new HashMap<String, String>();
//			paramMap.put("name", name);
//		}
//
//		if (!"".equals(status)) {
//			if (paramMap == null)
//				paramMap = new HashMap<String, String>();
//			paramMap.put("status", status);
//		}
//
//		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
//		// request
//
//		// @TODO 2. guitoken should be checked, timeout or not
//
//		try {
//			List<FloatingIP> list = floatingIPServiceImpl.getFloatingIPExtList(paramMap, guiToken);
//			return list;
//		} catch (BusinessException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		System.out.println("API耗时==========" + (System.currentTimeMillis() - start));
//
//		return null;
//
//	}
}
