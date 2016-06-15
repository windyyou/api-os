package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceConfig;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceDetail;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Volume;

public interface InstanceService {

	public List<InstanceDetail> getInstanceList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public InstanceDetail showInstance(Map<String,String> paramMap,String tokenId,Boolean details,InstanceDetail instanceInfoFromDB,HttpServletResponse response) throws BusinessException;
	public InstanceDetail createInstance(InstanceDetail instanceInfo,String guiTokenId,HttpServletResponse response) throws BusinessException;
	public void deleteInstance(String id,String guiTokenId,HttpServletResponse response) throws BusinessException;
	public List<Volume>  getAttachedVolumes(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Image createInstanceSnapshot(String id, String guiToken,String body,HttpServletResponse response) throws BusinessException;
	public InstanceConfig getInstanceConfig();
}
