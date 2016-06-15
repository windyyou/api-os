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
import com.fnst.cloudapi.pojo.openstackapi.forgui.VolumeConfig;
import com.fnst.cloudapi.pojo.openstackapi.forgui.VolumeType;
import com.fnst.cloudapi.service.openstackapi.InstanceService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.VolumeService;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class VolumeController {
	@Resource
	InstanceMapper instanceMapper;
	
	@Resource
	VolumeMapper volumeMapper;
	
	@Resource
	BackupMapper backupMapper;
	
	@RequestMapping(value = "/volumes", method = RequestMethod.GET)
	public String getVolumesList(
			@RequestHeader(value = "X-ApiAuth-Token", defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = "limit", defaultValue = "") String limit,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "status", defaultValue = "") String status,
			@RequestParam(value = "instance", defaultValue = "") String instanceId,HttpServletResponse response) {

		List<Volume> volumesFromDB = volumeMapper.selectList();
		if(null != volumesFromDB && 0 != volumesFromDB.size()){
			List<Volume> volumesWithInstance = new ArrayList<Volume>();
			for(Volume volume : volumesFromDB){
				String attachedInstanceId = volume.getInstanceId();
				if(null != attachedInstanceId && !attachedInstanceId.isEmpty()){
					Instance instance = instanceMapper.selectByPrimaryKey(attachedInstanceId);
					volume.addInstance(instance);	
				}
				setBackupInfo(volume);
				volumesWithInstance.add(volume);
			}
			JsonHelper<List<Volume>, String> jsonHelp = new JsonHelper<List<Volume>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(volumesWithInstance);
		}
		
		Map<String, String> paramMap = null;

		if (!"".equals(limit)) {
			paramMap = new HashMap<String, String>();
			paramMap.put("limit", limit);
		}

		if (!"".equals(name)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put("name", name);
		}

		if (!"".equals(status)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put("status", status);
		}

		if (!"".equals(instanceId)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put("instance", instanceId);
		}

		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
		// request

		// @TODO 2. guitoken should be checked, timeout or not

		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		try {
			List<Volume> volumes = volumeService.getVolumeList(paramMap, guiToken,response);
			if(null == volumes){
				ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
				return exception.getResponseMessage();
			}
			List<Volume> volumesWithInstance = new ArrayList<Volume>();
			for(Volume volume : volumes){
				if(null != volumeMapper.selectByPrimaryKey(volume.getId()))
					volumeMapper.updateByPrimaryKeySelective(volume);
				else
				    volumeMapper.insertSelective(volume);
				Instance instance = instanceMapper.selectByPrimaryKey(volume.getInstanceId());
				if(null != instance){
					volume.addInstance(instance);
				}else{
					if(null != volume.getInstanceId() && !volume.getInstanceId().isEmpty()){
						InstanceService inService = OsApiServiceFactory.getInstanceService();
						instance = inService.showInstance(paramMap, guiToken, false, null, null);
						volume.addInstance(instance);
					}
				}
				setBackupInfo(volume);
				volumesWithInstance.add(volume);
			}
			JsonHelper<List<Volume>, String> jsonHelp = new JsonHelper<List<Volume>, String>();
			return jsonHelp.generateJsonBodySimple(volumesWithInstance);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0006");
			return exception.getResponseMessage();
		} 
		
	}
	
	private void setBackupInfo(Volume volume){
		String backupId = volume.getBackupId();
		if(!Util.isNullOrEmptyValue(backupId)){
			String[] backupsId = backupId.split(",");
			for (int index = 0; index < backupsId.length; ++index) {
				Backup backup = backupMapper.selectByPrimaryKey(backupsId[index]);
				if (null == backup)
					continue; // TODO
				volume.addBackup(backup);
			}
		}
	}
		
	@RequestMapping(value = "/volumes/{id}", method = RequestMethod.GET)
	public String getVolume(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response){
		
		Volume volume = volumeMapper.selectByPrimaryKey(id);
		if(null != volume){
			setBackupInfo(volume);
			JsonHelper<Volume, String> jsonHelp = new JsonHelper<Volume, String>();
	    	return jsonHelp.generateJsonBodyWithEmpty(volume);
		}
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamConstant.ID, id);

		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		try {
			volume = volumeService.getVolume(paramMap, guiToken, response);
			if(null == volume){
				ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
				return exception.getResponseMessage();
			}
			volumeMapper.insertSelective(volume);
			setBackupInfo(volume);
			JsonHelper<Volume, String> jsonHelp = new JsonHelper<Volume, String>();
    		return jsonHelp.generateJsonBodyWithEmpty(volume);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0006");
			return exception.getResponseMessage();
		}	
	}
	
	@RequestMapping(value = "/volumes/config", method = RequestMethod.GET)
	public String getVolumeConfig(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			HttpServletResponse response){
		
		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		try {
			VolumeConfig volumeConfig = volumeService.getVolumeConfig(guiToken, response);
			if(null == volumeConfig){
				ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUMECONFIG_GET_0003");
				return exception.getResponseMessage();
			}
			JsonHelper<VolumeConfig, String> jsonHelp = new JsonHelper<VolumeConfig, String>();
    		return jsonHelp.generateJsonBodyWithEmpty(volumeConfig);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUMECONFIG_GET_0006");
			return exception.getResponseMessage();
		}	
	}
	
	@RequestMapping(value = "/volumes", method = RequestMethod.POST)
	public String createVolume(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String createBody, HttpServletResponse response){

		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		try {
			Volume volume = volumeService.createVolume(createBody, guiToken, response);
			if(null == volume){
				ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0003");
				return exception.getResponseMessage();
			}
			volumeMapper.insertSelective(volume);
			JsonHelper<Volume, String> jsonHelp = new JsonHelper<Volume, String>();
    		return jsonHelp.generateJsonBodyWithEmpty(volume);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUME_CREATE_0006");
			return exception.getResponseMessage();
		}	
	}
	
	@RequestMapping(value = "/volumes-type", method = RequestMethod.GET)
	public String getVolumesType(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = ParamConstant.NAME, defaultValue = "") String name,HttpServletResponse response){
		
		Map<String, String> paramMap = null;

		if (!"".equals(name)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.NAME, name);
		}
		
		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		List<VolumeType> list;
		try {
			list = volumeService.getVolumeTypeList(paramMap, guiToken,response);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0006");
			return exception.getResponseMessage();
		}    
		JsonHelper<List<VolumeType>, String> jsonHelp = new JsonHelper<List<VolumeType>, String>();
		return jsonHelp.generateJsonBodyWithEmpty(list);
	}
	
	@RequestMapping(value = "/volumes-type/{id}", method = RequestMethod.POST)
	public String getVolumeType(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id,HttpServletResponse response){
		
		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		VolumeType volumeType;
		try {
			volumeType = volumeService.getVolumeType(id, guiToken,response);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_GET_0006");
			return exception.getResponseMessage();
		}     
		JsonHelper<VolumeType, String> jsonHelp = new JsonHelper<VolumeType, String>();
		return jsonHelp.generateJsonBodyWithEmpty(volumeType);
	}
	
	@RequestMapping(value = "/volumes-type", method = RequestMethod.POST)
	public String createVolumeType(@RequestHeader(value = ParamConstant.AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String volumeTypeBody,HttpServletResponse response){
		
		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		VolumeType volumeType = null;
		try {
			volumeType = volumeService.createVolumeType(volumeTypeBody, guiToken,response);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_VOLUMETYPE_CREATE_0006");
			return exception.getResponseMessage();
		}     
		JsonHelper<VolumeType, String> jsonHelp = new JsonHelper<VolumeType, String>();
		return jsonHelp.generateJsonBodyWithEmpty(volumeType);
	}
	

}
