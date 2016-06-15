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

import com.fnst.cloudapi.dao.common.ImageMapper;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.service.openstackapi.ImageService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class ImageController {
	
	@Resource
	ImageMapper imageMapper;
	
	@RequestMapping(value = "/images", method = RequestMethod.GET)
	public String getImagesList(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.LIMIT, defaultValue = "") String limit,
			@RequestParam(value = ParamConstant.NAME, defaultValue = "") String name,
			@RequestParam(value = ParamConstant.STATUS, defaultValue = "") String status,
			@RequestParam(value = ParamConstant.VISIBILITY, defaultValue = "") String visibility,
			HttpServletResponse response) {

		List<Image> imagesFromDB = imageMapper.selectList();
		if(null != imagesFromDB && 0 != imagesFromDB.size()){
			JsonHelper<List<Image>, String> jsonHelp = new JsonHelper<List<Image>, String>();
    		return jsonHelp.generateJsonBodySimple(imagesFromDB);
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

		if (!"".equals(status)) {
			if (null == paramMap)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.STATUS, status);
		}

		if (!"".equals(visibility)) {
			if (null == paramMap) {
				paramMap = new HashMap<String, String>();
				paramMap.put(ParamConstant.VISIBILITY, visibility);
			}
		}

		// TODO
		try {
			ImageService resService = OsApiServiceFactory.getImageService();
			List<Image> images = resService.getImageList(paramMap, guiToken, response);
			if(null != images){
				for(Image image : images){
					imageMapper.insertSelective(image);
				}	
			}
			JsonHelper<List<Image>, String> jsonHelp = new JsonHelper<List<Image>, String>();
    		return jsonHelp.generateJsonBodySimple(images);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0006");
			return exception.getResponseMessage();
		}
	}

	@RequestMapping(value = "/images/{id}", method = RequestMethod.GET)
	public String getImage(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response) {

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamConstant.ID, id);

		ImageService resService = OsApiServiceFactory.getImageService();
		try {
			Image image = resService.getImage(paramMap, guiToken, response);
			imageMapper.insertSelective(image);
			JsonHelper<Image, String> jsonHelp = new JsonHelper<Image, String>();
    		return jsonHelp.generateJsonBodySimple(image);
		} catch (ResourceBusinessException e) {
			return e.getResponseMessage();
		} catch (Exception e) {
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0006");
			return exception.getResponseMessage();
		}
	}
}
