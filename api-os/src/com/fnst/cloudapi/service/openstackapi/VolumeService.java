package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Volume;
import com.fnst.cloudapi.pojo.openstackapi.forgui.VolumeConfig;
import com.fnst.cloudapi.pojo.openstackapi.forgui.VolumeType;

public interface VolumeService {
	
	public List<Volume> getVolumeList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Volume getVolume(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Volume createVolume(String createBody,String tokenId,HttpServletResponse response) throws BusinessException;
	
	public VolumeConfig getVolumeConfig(String tokenId,HttpServletResponse response) throws BusinessException;
	
	public List<VolumeType> getVolumeTypeList(Map<String,String> paramMap,String tokenId,HttpServletResponse response)throws BusinessException;
	public VolumeType createVolumeType(String createBody, String guiToken,HttpServletResponse response)throws BusinessException;
	public VolumeType getVolumeType(String volumeTypeId, String tokenId,HttpServletResponse response)throws BusinessException;
}
