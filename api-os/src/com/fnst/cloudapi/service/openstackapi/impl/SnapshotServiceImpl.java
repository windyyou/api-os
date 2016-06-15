package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.CloudConfig;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.service.openstackapi.SnapshotService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsBase;
import com.fnst.cloudapi.util.http.RequestUrlHelper;
import com.fnst.cloudapi.util.http.pool.OSHttpClientUtil;

@Service
public class SnapshotServiceImpl   implements SnapshotService {
	
	@Resource
	private CloudConfig cloudconfig;
	
	@Resource
	private OSHttpClientUtil oSHttpClientUtil;
	
	@Override
	public List<Image> getSnapshotList(Map<String, String> paramMap, String tokenId,HttpServletResponse response) throws BusinessException {
		
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot=osClient.getToken();
		   //token should have Regioninfo
		
		
		String region ="RegionOne";
		
		String url=ot.getEndPoint(TokenOs.EP_TYPE_IMAGE, region).getPublicURL();
		if(paramMap==null)
			paramMap = new HashMap();
	    paramMap.put("image_type",ParamConstant.SNAPSHOT_TYPE_IMAGE );
		url=RequestUrlHelper.createFullUrl(url+"/v2/images", paramMap);
		HashMap<String, String> headers = new HashMap<String, String>();
		//headers.put("X-Auth-Token" ,ot.getTokenid());
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN ,ot.getTokenid());
		
		Map<String, String>  rs = oSHttpClientUtil.httpDoGet(url, headers);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		if (null == rs)
			throw new ResourceBusinessException("CS_IMAGE_GET_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		List<Image> images = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				images = getImages(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_IMAGE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = oSHttpClientUtil.httpDoGet(url, headers);
			try {
				images = getImages(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_IMAGE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_IMAGE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_IMAGE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_IMAGE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_IMAGE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_IMAGE_GET_0006");
		}
		return images;
	}
	
	@Override
	public Image getImage(String imageId, String tokenId,HttpServletResponse response) throws BusinessException {
		
		HttpClientForOsBase osClient = new HttpClientForOsBase(cloudconfig);
		TokenOs ot = osClient.getToken();
		   //token should have Regioninfo
		
		String region ="RegionOne";
		
		String url=ot.getEndPoint(TokenOs.EP_TYPE_IMAGE, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/v2/images/");
		sb.append(imageId);
		
		url=RequestUrlHelper.createFullUrl(sb.toString(), null);
		HashMap<String, String> headers = new HashMap<String, String>();
		//headers.put("X-Auth-Token" ,ot.getTokenid());
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN ,ot.getTokenid());
		
		Map<String, String>  rs = oSHttpClientUtil.httpDoGet(url, headers);
//		Map<String, String>  rs =client.httpDoGet(url, ot.getTokenid());
		
		if (null == rs)
			throw new ResourceBusinessException("CS_IMAGE_GET_0003");// TODO throw exception

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		Image image = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode imageNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				image = getImageInfo(imageNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_IMAGE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = oSHttpClientUtil.httpDoGet(url, headers);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode imageNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				image = getImageInfo(imageNode);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_IMAGE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_IMAGE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_IMAGE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_IMAGE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_IMAGE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_IMAGE_GET_0006");
		}
		return image;
	}
	
	private List<Image> getImages(Map<String, String> rs) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode imagesNode = rootNode.path(ResponseConstant.IMAGES);
		int imagesCount = imagesNode.size();
		if (0 == imagesCount)
			return null;
		List<Image> images = new ArrayList<Image>();
		for (int index = 0; index < imagesCount; ++index) {
			Image image = getImageInfo(imagesNode.get(index));
			images.add(image);
		}
		return images;
	}
	
	private Image getImageInfo(JsonNode imageNode){
		if(null == imageNode)
			return null;
		Image image = new Image();
		image.setId(imageNode.path(ResponseConstant.ID).textValue());
		image.setName(imageNode.path(ResponseConstant.NAME).textValue());
		image.setStatus(imageNode.path(ResponseConstant.STATUS).textValue());
		image.setSize(Integer.toString(imageNode.path(ResponseConstant.SIZE).intValue()));
		image.setCreatedAt(imageNode.path(ResponseConstant.CREATED_AT).textValue());
		//image.setCreatedAt(DateHelper.getDateByString(oneSnapshot.path(ResponseConstant.CREATED_AT).textValue()).toString());
		
		return image;
	}

}
