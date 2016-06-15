package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Backup;

public interface BackupService {

	public List<Backup> getBackupList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Backup getBackup(String backupId,String tokenId,HttpServletResponse response) throws BusinessException;
	public Backup createBackup(String createBody,String tokenId,HttpServletResponse response) throws BusinessException;
}
