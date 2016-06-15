package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Backup;

public interface BackupMapper extends SuperMapper<Backup, String> {
	public Integer countNum();

	public Integer countNumByInstanceStatus(String status);
	
	public Backup selectByInstanceId(String id);
	
	public List<Backup> selectListByInstanceId(String id);
	
	public List<Backup> selectList();

	public List<Backup> selectListForPage(int start, int end);
}
