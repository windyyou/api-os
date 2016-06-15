package com.fnst.cloudapi.controller.openstackapi;

import java.util.ArrayList;
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

import com.fnst.cloudapi.dao.common.ImageMapper;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.service.openstackapi.SnapshotService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

/**
 * 
 * snapshot操作
 *
 */
@RestController
public class SnapshotController {

	@Resource
	private SnapshotService snapshotServiceImpl;

	@Resource
	private ImageMapper imageMapper;

	@RequestMapping(value = "/snapshots", method = RequestMethod.GET)
	public String getSnapshotList(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.LIMIT, defaultValue = "") String limit,
			@RequestParam(value = ParamConstant.NAME, defaultValue = "") String name,
			@RequestParam(value = ParamConstant.STATUS, defaultValue = "") String status,
			HttpServletResponse response) {

		List<Image> images = imageMapper.selectList();
		if(null != images){
			JsonHelper<List<Image>, String> jsonHelp = new JsonHelper<List<Image>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(images);
		}
		
		Map<String, String> paramMap = null;

		if (!"".equals(limit)) {
			paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.LIMIT, limit);
		}

		if (!"".equals(name)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.NAME, name);
		}

		if (!"".equals(status)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.STATUS, status);
		}

		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
		// request

		// @TODO 2. guitoken should be checked, timeout or not
		try {
			images = snapshotServiceImpl.getSnapshotList(paramMap, guiToken, response);
			if(null == images){
				images = new ArrayList<Image>();
				JsonHelper<List<Image>, String> jsonHelp = new JsonHelper<List<Image>, String>();
				return jsonHelp.generateJsonBodyWithEmpty(images);
			}
			for(Image image : images){
				if(null != imageMapper.selectByPrimaryKey(image.getId()))
					imageMapper.updateByPrimaryKeySelective(image);
				else
					imageMapper.insertSelective(image);
			}
			JsonHelper<List<Image>, String> jsonHelp = new JsonHelper<List<Image>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(images);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_IMAGE_GET_0006");
			return exception.getResponseMessage();
		} 
	}
	
	@RequestMapping(value = "/snapshots/{id}", method = RequestMethod.GET)
	public String getSnapshot(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response){
		
		Image image = imageMapper.selectByPrimaryKey(id);
		if(null != image){
			JsonHelper<Image, String> jsonHelp = new JsonHelper<Image, String>();
			return jsonHelp.generateJsonBodyWithEmpty(image);
		}
		
		try {
			image = snapshotServiceImpl.getImage(id, guiToken, response);
			if(null == image){
				ResourceBusinessException exception = new ResourceBusinessException("CS_IMAGE_GET_0003"); // TODO// change//the/message
				response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
				return exception.getResponseMessage();
			}
			if(null != imageMapper.selectByPrimaryKey(id))
				imageMapper.updateByPrimaryKeySelective(image);
			else
				imageMapper.insertSelective(image);
			JsonHelper<Image, String> jsonHelp = new JsonHelper<Image, String>();
    		return jsonHelp.generateJsonBodySimple(image);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_IMAGE_GET_0006");
			return exception.getResponseMessage();
		}	
	}

	@RequestMapping(value = "/snapshots", method = RequestMethod.POST)
	public Image createSnapshot() {
		return null;

	}

}
