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
import com.fnst.cloudapi.pojo.common.Util;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Volume;
import com.fnst.cloudapi.pojo.openstackapi.forgui.VolumeConfig;
import com.fnst.cloudapi.pojo.openstackapi.forgui.VolumeType;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.VolumeService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class VolumeServiceImpl extends CloudConfigAndTokenHandler implements VolumeService {

	private HttpClientForOsRequest client = null;

	public VolumeServiceImpl() {
		super();
		this.client = new HttpClientForOsRequest();
	}

	@Override
	public List<Volume> getVolumeList(Map<String, String> paraMap, String tokenId,HttpServletResponse response) throws BusinessException {

		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_VOLUMEV2, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/volumes/detail", paraMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());


		List<Volume> volumes = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				volumes = getVolumes(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			try {
				volumes = getVolumes(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0006");
		}

		return volumes;
	}

	@Override
	public Volume getVolume(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException
	{
		String volumeId = paramMap.get(ParamConstant.ID);
		
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_VOLUMEV2, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/volumes/");
		sb.append(volumeId);
		url = RequestUrlHelper.createFullUrl(sb.toString(), null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());

		System.out.println("httpcode:" + rs.get("httpcode"));
		System.out.println("jsonbody:" + rs.get("jsonbody"));

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		Volume volume = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumeNode = rootNode.path(ResponseConstant.VOLUME);
				volume = getVolumeInfo(volumeNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumeNode = rootNode.path(ResponseConstant.VOLUME);
				volume = getVolumeInfo(volumeNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0006");
		}

		return volume;
	}
	
	@Override
	public Volume createVolume(String createBody,String tokenId,HttpServletResponse response) throws BusinessException{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_VOLUMEV2, region).getPublicURL();
		url += "/volumes";

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoPost(url, headers,createBody);
	
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		Volume volume = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_ASYNC_RESPONSE_CODE:{
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumeNode = rootNode.path(ResponseConstant.VOLUME);
				volume = getVolumeInfo(volumeNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0003");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(url, headers,createBody);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if(null != response)
			   response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumeNode = rootNode.path(ResponseConstant.VOLUME);
				volume = getVolumeInfo(volumeNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0003");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0006");
		}

		return volume;
	}
	
	private List<Volume> getVolumes(Map<String, String> rs) throws JsonProcessingException, IOException, BusinessException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode volumesNode = rootNode.path(ResponseConstant.VOLUMES);
		int volumesCount = volumesNode.size();
		if (0 == volumesCount)
			return null;
		
		List<Volume> volumes = new ArrayList<Volume>();
		for (int index = 0; index < volumesCount; ++index) {
			Volume volume = getVolumeInfo(volumesNode.get(index));
			volumes.add(volume);
		}
		
		return volumes;
	}
	
	private Volume getVolumeInfo(JsonNode volumeNode) throws BusinessException{
		if(null == volumeNode)
			return null;
		Volume volume = new Volume();
		volume.setId(volumeNode.path(ResponseConstant.ID).textValue());
		volume.setName(volumeNode.path(ResponseConstant.NAME).textValue()); 
		volume.setType(volumeNode.path(ResponseConstant.VOLUME_TYPE).textValue());
		volume.setCreatedAt(volumeNode.path(ResponseConstant.CREATED_AT).textValue());
		volume.setSize(Integer.toString(volumeNode.path(ResponseConstant.SIZE).intValue()));
		volume.setStatus(volumeNode.path(ResponseConstant.STATUS).textValue());
		
		JsonNode attachments = volumeNode.path(ResponseConstant.ATTACHMENTS);
		if(null == attachments || 0 ==attachments.size())
			return volume;
		
		JsonNode attachment = attachments.get(0);
		volume.setInstanceId(attachment.path(ResponseConstant.SERVER_ID).textValue());
		return volume;
	}
	
	@Override
	public List<VolumeType> getVolumeTypeList(Map<String, String> paraMap, String tokenId,HttpServletResponse response)throws BusinessException{

		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne"; // we should get the regioninfo by the
										// guiTokenId

		String url = ot.getEndPoint(TokenOs.EP_TYPE_VOLUMEV2, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/types", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);

		List<VolumeType> volumeTypes = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				volumeTypes = getVolumeTypes(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if(null != response)
			   response.setStatus(httpCode);
			try {
				volumeTypes = getVolumeTypes(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0006");
		}

		return volumeTypes;
	}

	@Override
	public VolumeType getVolumeType(String volumeTypeId, String tokenId,HttpServletResponse response)throws BusinessException{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		// String url=ot.getEndPoint(TokenOs.EP_TYPE_NETWORK,
		// region).getPublicURL();
		// url=url+"/v2.0/networks/" + NetworkId;
		String url = ot.getEndPoint(TokenOs.EP_TYPE_NETWORK, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/types/");
		sb.append(volumeTypeId);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		
		Map<String, String> rs = client.httpDoGet(sb.toString(), headers);
		VolumeType volumeType = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumeTypeNode = rootNode.path(ResponseConstant.VOLUME_TYPE);
				volumeType = getVolumeTypeInfo(volumeTypeNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs =  client.httpDoGet(sb.toString(), headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if(null != response)
			   response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumeTypeNode = rootNode.path(ResponseConstant.VOLUME_TYPE);
				volumeType = getVolumeTypeInfo(volumeTypeNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0006");
		}

		return volumeType;
	}
	
	@Override
	public VolumeType createVolumeType(String createBody, String guiToken,HttpServletResponse response) throws BusinessException {
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne"; // we should get the regioninfo by the guiTokenId

		String url = ot.getEndPoint(TokenOs.EP_TYPE_VOLUMEV2, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/types", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		
		Map<String, String> rs = client.httpDoPost(url, headers,createBody);

		VolumeType volumeType = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumeTypeNode = rootNode.path(ResponseConstant.VOLUME_TYPE);
				volumeType = getVolumeTypeInfo(volumeTypeNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(url, headers,createBody);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if(null != response)
			   response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumeTypeNode = rootNode.path(ResponseConstant.VOLUME_TYPE);
				volumeType = getVolumeTypeInfo(volumeTypeNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_CREATE_0006");
		}

		return volumeType;
	}
	
	public VolumeConfig getVolumeConfig(String tokenId,HttpServletResponse response) throws BusinessException{
		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		List<VolumeType> volumeTypes = volumeService.getVolumeTypeList(null, tokenId,response);
		if(Util.isNullOrEmptyList(volumeTypes))
			throw new ResourceBusinessException("CS_COMPUTE_VOLUMECONFIG_GET_0003");
		
		VolumeConfig volumeConfig = new VolumeConfig();
		volumeConfig.setType(volumeTypes);
		return volumeConfig;
	}
	
	private VolumeType getVolumeTypeInfo(JsonNode volumeTypeNode){
		VolumeType volumeTypeInfo = new VolumeType();
		volumeTypeInfo.setId(volumeTypeNode.path(ResponseConstant.ID).textValue());
		volumeTypeInfo.setName(volumeTypeNode.path(ResponseConstant.NAME).textValue());
		volumeTypeInfo.setName(volumeTypeNode.path(ResponseConstant.NAME).textValue());
		volumeTypeInfo.setDescription(volumeTypeNode.path(ResponseConstant.DESCRIPTION).textValue());
		volumeTypeInfo.setIs_public(volumeTypeNode.path(ResponseConstant.IS_PUBLIC).textValue());
		volumeTypeInfo.setUnitPrice("0.01"); //TODO
		JsonNode extraSpecNode = volumeTypeNode.path(ResponseConstant.EXTRA_SPECS);
		if(null != extraSpecNode){
			String backendName = extraSpecNode.path(ResponseConstant.VOLUME_BACKEND_NAME).textValue(); //TODO change it later
			List<String> extraSpecs = new ArrayList<String>();
			extraSpecs.add(backendName);
			volumeTypeInfo.setExtra_Specs(extraSpecs);
		}
		
		return volumeTypeInfo;
	}
	
	private List<VolumeType> getVolumeTypes(Map<String, String> rs) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode volumeTypesNode = rootNode.path(ResponseConstant.VOLUME_TYPES);
		int volumesTypeCount = volumeTypesNode.size();
		if (0 == volumesTypeCount)
			return null;
		List<VolumeType> volumeTypes = new ArrayList<VolumeType>();
		for (int index = 0; index < volumesTypeCount; ++index) {
			VolumeType volumeType = getVolumeTypeInfo(volumeTypesNode.get(index));
			volumeTypes.add(volumeType);
		}
		return volumeTypes;
	}

	
}
