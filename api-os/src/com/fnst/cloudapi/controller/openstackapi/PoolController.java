package com.fnst.cloudapi.controller.openstackapi;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.openstackapi.forgui.PoolEntity;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Stack;
import com.fnst.cloudapi.pojo.openstackapi.forgui.StackResource;
import com.fnst.cloudapi.service.openstackapi.PoolEntityService;
import com.fnst.cloudapi.service.openstackapi.impl.StackServiceImpl;
import com.fnst.cloudapi.service.pool.PoolResourceService;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class PoolController {
	@Resource
	PoolEntityService poolEntityService;
	@Resource
	PoolResourceService poolResourceService;

	@RequestMapping(value = "/pools", method = RequestMethod.GET)
	public String getPools(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			HttpServletResponse response) {

		List<PoolEntity> pl = this.poolEntityService.listPoolEntity(guiToken);
		return PoolEntity.toJSON(pl);
	}

	@RequestMapping(value = "/pools/{pool_id}", method = RequestMethod.GET)
	public String getPool(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String pool_id, HttpServletResponse response) {
		PoolEntity p = this.poolEntityService.getPoolEntityById(pool_id);
		return PoolEntity.toJSON(p);
	}

	@RequestMapping(value = "/pools", method = RequestMethod.POST)
	public String createPool(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String poolBody, HttpServletResponse response) {

		if (null == poolBody || "".equals(poolBody))
			return null; // TODO throw exception

		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> params = null;
		try {
			params = mapper.readValue(poolBody, new TypeReference<HashMap<String, Object>>() {
			});
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		PoolEntity p = poolEntityService.createPoolEntity(params, guiToken);
		return PoolEntity.toJSON(p);
	}

	@RequestMapping(value = "/stacks", method = RequestMethod.POST)
	public Stack createStack(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String poolBody, HttpServletResponse response) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> params = mapper.readValue(poolBody, new TypeReference<HashMap<String, Object>>() {
			});
			return this.poolResourceService.create(params, guiToken);
		} catch (JsonParseException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("Stack Create Failed");
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "stacks/{stack_id}", method = RequestMethod.GET)
	public String getStackResources(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String stack_id, HttpServletResponse response) {
		try {
			List<StackResource> list = this.poolResourceService.getResources(stack_id, guiToken);
			StackServiceImpl stackService = new StackServiceImpl();
			Stack s = stackService.getStack(stack_id, guiToken);
//			String stack = Stack.toJSON(s);
//			String resource = StackResource.toJSON(list);
//			String json = "{\"stack\":" + stack + ",\"resources\":" + resource + "}";
			return StackResource.toJSON(s, list);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	@RequestMapping(value = "/stacks", method = RequestMethod.GET)
	public String getStacks(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			HttpServletResponse response) {
		try {
			StackServiceImpl stackService = new StackServiceImpl();
			List<Stack> sl = stackService.getStackList(null, guiToken);
			return Stack.toJSON(sl);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "/pools/config", method = RequestMethod.GET)
	public String getPoolConfig(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			HttpServletResponse response) {
		try {
			String fake = "{\"unitPrice\":{\"core\":0.25,\"ram\":0.15,\"CTCCIps\":1,\"CMCCIps\":1,\"CUCCIps\":1,\"BGPIps\":1,\"performance\":0.2,\"capacity\":0.05,\"database\":1,\"alarm\":1,\"VPN\":1,\"loadbalancers\":1,\"firewalls\":1}}";
			
			return fake;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@RequestMapping(value = "/stacks/config", method = RequestMethod.GET)
	public String getStackConfig(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			HttpServletResponse response) {
		try {
			String fake = "{\"ipVersion\":[\"IP v4\",\"IP v6\"],\"core\":{\"core\":[1,2,4,8,12,16]},\"ram\":{\"size\":[1024,2048,4096,8192,12288,16384,24576,32768,40960]},\"volume\":{\"size\":20,\"type\":[{\"id\":\"qwerqwer123412341234\",\"name\":\"performance\"},{\"id\":\"qwerqwer123412341235\",\"name\":\"capacity\"}]},\"instanceType\":[{\"id\":\"qwerqwer123412341266\",\"name\":\"performance\"},{\"id\":\"qwerqwer123412341267\",\"name\":\"highPerformance\"}]}";
			return fake;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	// //get all tenants quota
	// private List<Quota> getTenantQuotas(String adminTokenId){
	//
	// QuotaService quotaService = OsApiServiceFactory.getQuotaService();
	// List<Quota> quotas = quotaService.getQuotas(null, adminTokenId);
	// if(null == quotas)
	// return null;
	// int allocatedVcpu = 0;
	// int allocatedRam = 0;
	// int allocatedFloatingIP = 0;
	// for(Quota quota : quotas){
	// allocatedVcpu +=
	// quota.getQuotaDetails().get(ParamConstant.CORES_LIMIT).getLimit();
	// allocatedRam +=
	// quota.getQuotaDetails().get(ParamConstant.RAM_LIMIT).getLimit();
	// allocatedRam +=
	// quota.getQuotaDetails().get(ParamConstant.FLOATING_IPS_LIMIT).getLimit();
	// }
	// return quotas;
	// }

}
