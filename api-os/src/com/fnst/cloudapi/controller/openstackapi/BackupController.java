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

import com.fnst.cloudapi.dao.common.BackupMapper;
import com.fnst.cloudapi.dao.common.InstanceMapper;
import com.fnst.cloudapi.dao.common.VolumeMapper;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.Util;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Backup;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Instance;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Volume;
import com.fnst.cloudapi.service.openstackapi.BackupService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class BackupController {

	@Resource
	VolumeMapper volumeMapper;
	
	@Resource
	BackupMapper backupMapper;
	
	@Resource
	InstanceMapper instanceeMapper;
	
	@RequestMapping(value = "/backups", method = RequestMethod.GET)
	public String getBackupsList(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.LIMIT, defaultValue = "") String limit,
			@RequestParam(value = ParamConstant.NAME, defaultValue = "") String name,
			@RequestParam(value = ParamConstant.STATUS, defaultValue = "") String status,
			@RequestParam(value = ParamConstant.VOLUME, defaultValue = "") String volumeId,HttpServletResponse response) {

		List<Backup> backupsFromDB = backupMapper.selectList();
		if(!Util.isNullOrEmptyList(backupsFromDB)){
			List<Backup> backupsWithVolume = new ArrayList<Backup>();
			for(Backup backup : backupsFromDB){
				String attchedVolumeId = backup.getVolumeId();
				if(null != attchedVolumeId && !attchedVolumeId.isEmpty()){
					Volume volume = volumeMapper.selectByPrimaryKey(attchedVolumeId);
					if(null != volume){
				//		volume.setBackupId(Util.getIdWithAppendId(attchedVolumeId,volume.getBackupId()));
				//		volumeMapper.updateByPrimaryKeySelective(volume);
						backup.setVolume(volume);	
					}
				}
				backupsWithVolume.add(backup);
			}
			JsonHelper<List<Backup>, String> jsonHelp = new JsonHelper<List<Backup>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(backupsWithVolume);
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

		if (!"".equals(volumeId)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.VOLUME, volumeId);
		}
		
		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
		// request

		// @TODO 2. guitoken should be checked, timeout or not

		BackupService backupService = OsApiServiceFactory.getBackupService();
		try {
			List<Backup> backups = backupService.getBackupList(paramMap, guiToken,response);
			if(null == backups){
				List<Backup> backupsWithVolume = new ArrayList<Backup>();
				JsonHelper<List<Backup>, String> jsonHelp = new JsonHelper<List<Backup>, String>();
				return jsonHelp.generateJsonBodyWithEmpty(backupsWithVolume);
			}
			List<Backup> backupsWithVolume = new ArrayList<Backup>();
			for(Backup backup : backups){
				if(null != backupMapper.selectByPrimaryKey(backup.getId()))
					backupMapper.updateByPrimaryKeySelective(backup);
				else
					backupMapper.insertSelective(backup);
				Volume volume = volumeMapper.selectByPrimaryKey(backup.getVolumeId());
				backup.setVolume(volume);
				backupsWithVolume.add(backup);
			}
			JsonHelper<List<Backup>, String> jsonHelp = new JsonHelper<List<Backup>, String>();
			return jsonHelp.generateJsonBodySimple(backupsWithVolume);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0006");
			return exception.getResponseMessage();
		} 
	}
	
	@RequestMapping(value = "/backups/{id}", method = RequestMethod.GET)
	public String getBackup(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response) {
        Backup backupFromDB = backupMapper.selectByPrimaryKey(id);
        if(null != backupFromDB){
        	JsonHelper<Backup, String> jsonHelp = new JsonHelper<Backup, String>();
			return jsonHelp.generateJsonBodyWithEmpty(backupFromDB);
        }
		
		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
		// request

		// @TODO 2. guitoken should be checked, timeout or not

		BackupService backupService = OsApiServiceFactory.getBackupService();
		try {
			Backup backup = backupService.getBackup(id, guiToken,response);
			if(null == backup){
				JsonHelper<Backup, String> jsonHelp = new JsonHelper<Backup, String>();
				return jsonHelp.generateJsonBodyWithEmpty(new Backup());
			}
		    if(null != backupMapper.selectByPrimaryKey(backup.getId()))
				backupMapper.updateByPrimaryKeySelective(backup);
			else
				backupMapper.insertSelective(backup);
			JsonHelper<Backup, String> jsonHelp = new JsonHelper<Backup, String>();
			return jsonHelp.generateJsonBodyWithEmpty(backup);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_GET_0006");
			return exception.getResponseMessage();
		} 
	}
	
	@RequestMapping(value = "/backups", method = RequestMethod.POST)
	public String createBackup(
			@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String createBody, HttpServletResponse response) {

		BackupService backupService = OsApiServiceFactory.getBackupService();
		try {
			Backup backup = backupService.createBackup(createBody, guiToken,response);
			if(null == backup){
				ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0006");
				return exception.getResponseMessage();
			}
			backupMapper.insertSelective(backup);
			
			Volume volume = volumeMapper.selectByPrimaryKey(backup.getVolumeId());
			if(null == volume){
				ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0006");
				return exception.getResponseMessage();
			}
			String backupId = Util.getIdWithAppendId(backup.getId(),volume.getBackupId());
			volume.setBackupId(backupId);
			volumeMapper.updateByPrimaryKeySelective(volume);
			volume.addBackup(backup);
			String instanceId = volume.getInstanceId();
			Instance instance = instanceeMapper.selectByPrimaryKey(instanceId);
			if(null != instance)
				volume.addInstance(instance);
			JsonHelper<Volume, String> jsonHelp = new JsonHelper<Volume, String>();
			return jsonHelp.generateJsonBodyWithEmpty(volume);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_BACKUP_CREATE_0003");
			return exception.getResponseMessage();
		} 
	}
	
}
