package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.ImageService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class ImageServiceImpl extends CloudConfigAndTokenHandler implements ImageService {
	private HttpClientForOsRequest httpClient = null;

	public ImageServiceImpl() {
		httpClient = new HttpClientForOsRequest();
	}

	@Override
	public List<Image> getImageList(Map<String, String> paraMap, String guiTokenId,HttpServletResponse response) throws BusinessException {

		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";// we should get the regioninfo by the
									// guiTokenId

		String url = ot.getEndPoint(TokenOs.EP_TYPE_IMAGE, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2/images", paraMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = httpClient.httpDoGet(url, headers);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());
		
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		    response.setStatus(httpCode);
		List<Image> list = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				list = getImages(rs);
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = httpClient.httpDoGet(url, headers);
			try {
				list = getImages(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0006");
		}
		return list;
	}

	public Image getImage(Map<String, String> paramMap, String tokenId, HttpServletResponse response)
			throws BusinessException {
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";// we should get the regioninfo by the guiTokenId
		String url = ot.getEndPoint(TokenOs.EP_TYPE_IMAGE, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/v2/images/");
		sb.append(paramMap.get(ParamConstant.ID));
		url = RequestUrlHelper.createFullUrl(sb.toString(), null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());
		Map<String, String> rs = httpClient.httpDoGet(url, headers);

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		    response.setStatus(httpCode);
		Image image = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode imageNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				image = getImageInfo(imageNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = httpClient.httpDoGet(url, headers);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode imageNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				image = getImageInfo(imageNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_GET_0006");
		}

		return image;
	}

	private List<Image> getImages(Map<String, String> rs) throws JsonProcessingException, IOException{
		List<Image> images = new ArrayList<Image>();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode imagesNode = rootNode.path(ResponseConstant.IMAGES);
		for (int index = 0; index < imagesNode.shortValue(); ++index) {
			JsonNode imageNode = imagesNode.get(index);
			Image imageInfo = getImageInfo(imageNode);
			if(null == imageInfo)
				continue;
			images.add(imageInfo);
		}
		return images;
	}
	
	private Image getImageInfo(JsonNode imageNode) {
		if (null == imageNode)
			return null;
		Image imageInfo = new Image();
		imageInfo.setId(imageNode.path(ResponseConstant.ID).textValue());
		imageInfo.setName(imageNode.path(ResponseConstant.NAME).textValue());
		// imageInfo.setTags(imageNode.path(ResponseConstant.TAGS).textValue());
		imageInfo.setVisibility(imageNode.path(ResponseConstant.VISIBILITY).textValue());
		imageInfo.setDiskFormat(imageNode.path(ResponseConstant.DISK_FORMAT).textValue());
		imageInfo.setMinDisk(Integer.toString(imageNode.path(ResponseConstant.MIN_DISK).intValue()));
		imageInfo.setMinRam(Integer.toString(imageNode.path(ResponseConstant.MIN_RAM).intValue()));
		imageInfo.setSize(Integer.toString(imageNode.path(ResponseConstant.SIZE).intValue()));
		imageInfo.setFile(imageNode.path(ResponseConstant.FILE).textValue());
		imageInfo.setOwner(imageNode.path(ResponseConstant.OWNER).textValue());
		imageInfo.setStatus(imageNode.path(ResponseConstant.STATUS).textValue());
		// // SimpleDateFormat dateFormat = new
		// SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		imageInfo.setCreatedAt(imageNode.path(ResponseConstant.CREATED_AT).textValue());
		imageInfo.setUpdatedAt(imageNode.path(ResponseConstant.UPDATED_AT).textValue());
		return imageInfo;
	}
}
