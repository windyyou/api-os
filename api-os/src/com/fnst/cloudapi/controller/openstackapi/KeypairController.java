package com.fnst.cloudapi.controller.openstackapi;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fnst.cloudapi.dao.common.KeypairMapper;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.Util;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Keypair;
import com.fnst.cloudapi.service.openstackapi.KeypairService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class KeypairController {

	@Resource
	KeypairMapper keypairMapper;

	@RequestMapping(value = "/keypairs", method = RequestMethod.GET)
	public String getKeypairList(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.LIMIT, defaultValue = "") String limit,
			@RequestParam(value = ParamConstant.NAME, defaultValue = "") String name, HttpServletResponse response) {

		List<Keypair> keypairsFromDB = keypairMapper.selectList();
		if (null != keypairsFromDB && 0 != keypairsFromDB.size()) {
			JsonHelper<List<Keypair>, String> jsonHelp = new JsonHelper<List<Keypair>, String>();
			return jsonHelp.generateJsonBodySimple(keypairsFromDB);
		}

		Map<String, String> paramMap = null;

		if (!"".equals(limit)) {
			paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.LIMIT, limit);
		}

		if (!"".equals(name)) {
			if (null == paramMap)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.OWNER, name);
		}

		KeypairService resService = OsApiServiceFactory.getkeypairService();
		try {
			List<Keypair> keypairs = resService.getKeypairList(paramMap, guiToken, response);
			for(Keypair keypair : keypairs){
				keypairMapper.insertSelective(keypair);
			}
			JsonHelper<List<Keypair>, String> jsonHelp = new JsonHelper<List<Keypair>, String>();
			return jsonHelp.generateJsonBodySimple(keypairs);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0006");
			return exception.getResponseMessage();
		}
	
	}

	@RequestMapping(value = "/keypairs/{name}", method = RequestMethod.GET)
	public String getKeypair(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String name,HttpServletResponse response) {

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamConstant.NAME, name);

		KeypairService resService = OsApiServiceFactory.getkeypairService();
		try {
			Keypair keypair = resService.getKeypair(paramMap, guiToken, response);
			keypairMapper.insertSelective(keypair);
			JsonHelper<Keypair, String> jsonHelp = new JsonHelper<Keypair, String>();
			return jsonHelp.generateJsonBodySimple(keypair);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0006");
			return exception.getResponseMessage();
		}
	}

	@RequestMapping(value = "/keypairs", method = RequestMethod.POST)
	public String createKeypair(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.NAME, defaultValue = "") String name,
			@RequestParam(value = ParamConstant.PUBLIC_KEY, defaultValue = "") String public_key,HttpServletResponse response) {

		Map<String, String> paramMap = null;

		name = "testKeyGen4";
		if (!"".equals(name)) {
			paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.NAME, name);
		}

		if (!"".equals(public_key)) {
			if (null == paramMap)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.PUBLIC_KEY, public_key);
		}

		KeypairService resService = OsApiServiceFactory.getkeypairService();
		try {
			Keypair keypair = resService.createKeypair(paramMap, guiToken, response);
			if(null == keypair){
				ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0003");
				return exception.getResponseMessage();
			}
			keypair.setCreated_at(Util.getCurrentDate());
			keypairMapper.insertSelective(keypair);
			JsonHelper<Keypair, String> jsonHelp = new JsonHelper<Keypair, String>();
			return jsonHelp.generateJsonBodySimple(keypair);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0006");
			return exception.getResponseMessage();
		}
	}
}
