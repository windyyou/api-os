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
import com.fnst.cloudapi.pojo.openstackapi.forgui.Backup;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Volume;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.BackupService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.VolumeService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class BackupServiceImpl extends CloudConfigAndTokenHandler implements BackupService {
	
	private HttpClientForOsRequest client = null;
	
	public BackupServiceImpl() {
		super();
		this.client = new HttpClientForOsRequest();
	}

	@Override
	public List<Backup> getBackupList(Map<String, String> paraMap, String tokenId,HttpServletResponse response) throws BusinessException{
		
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_VOLUMEV2, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/backups/detail", paraMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);

		List<Backup> backups = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				backups = getBackups(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0003");
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
				backups = getBackups(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0006");
		}
		return backups;
	}
	
	@Override
	public Backup getBackup(String backupId,String tokenId,HttpServletResponse response) throws BusinessException{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_VOLUMEV2, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/backups/");
		sb.append(backupId);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(sb.toString(), headers);

		Backup backup = null;
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		   response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode backupNode = rootNode.path(ResponseConstant.BACKUP);
				backup = getBackupInfo(backupNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if(null != response)
			   response.setStatus(httpCode);
			rs = client.httpDoGet(sb.toString(), headers);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode backupNode = rootNode.path(ResponseConstant.BACKUP);
				backup = getBackupInfo(backupNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0006");
		}
		return backup;
	}
	
	@Override
	public Backup createBackup(String createBody,String tokenId,HttpServletResponse response) throws BusinessException{
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";
		// String url=ot.getEndPoint(TokenOs.EP_TYPE_NETWORK,
		// region).getPublicURL();
		// url=url+"/v2.0/networks/" + NetworkId;
		String url = ot.getEndPoint(TokenOs.EP_TYPE_VOLUMEV2, region).getPublicURL();
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());
		Map<String, String> rs = client.httpDoPost(url + "/backups", ot.getTokenid(), createBody);
		if (null == rs)
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0003");

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		Backup backup = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_ASYNC_RESPONSE_CODE: {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode backupNode = rootNode.path(ResponseConstant.BACKUP);
				backup = getBackupInfo(backupNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(url + "/backups", ot.getTokenid(), createBody);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode backupNode = rootNode.path(ResponseConstant.BACKUP);
				backup = getBackupInfo(backupNode);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0006");
		}
		return backup;
	}
	
	private List<Backup> getBackups(Map<String, String> rs) throws JsonProcessingException, IOException, BusinessException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode backupsNode = rootNode.path(ResponseConstant.BACKUPS);
		int backupsCount = backupsNode.size();
		if (0 == backupsCount)
			return null;
		
		List<Backup> backups = new ArrayList<Backup>();
		for (int index = 0; index < backupsCount; ++index) {
			Backup backup = getBackupInfo(backupsNode.get(index));
			if(null == backup)
				continue;
			backups.add(backup);
		}
		
		return backups;
	}
	
	private Backup getBackupInfo(JsonNode backupNode){
		if(null == backupNode)
			return null;
		Backup backup = new Backup();
		backup.setId(backupNode.path(ResponseConstant.ID).textValue());
		backup.setName(backupNode.path(ResponseConstant.NAME).textValue()); 
		backup.setCreatedAt(backupNode.path(ResponseConstant.CREATED_AT).textValue());
		backup.setSize(Integer.toString(backupNode.path(ResponseConstant.SIZE).intValue()));
		backup.setStatus(backupNode.path(ResponseConstant.STATUS).textValue());
		backup.setVolumeId(backupNode.path(ResponseConstant.VOLUME_ID).textValue());
		
		return backup;
	}
	
}
