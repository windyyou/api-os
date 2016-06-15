package com.fnst.cloudapi.dao.common;

import java.util.List;


import com.fnst.cloudapi.pojo.openstackapi.forgui.Volume;

public interface VolumeMapper extends SuperMapper<Volume, String> {
	public Integer countNum();

	public Integer countNumByInstanceStatus(String status);
	
	public Volume selectByInstanceId(String id);
	
	public List<Volume> selectListByInstanceId(String id);
	
	public List<Volume> selectList();

	public List<Volume> selectListForPage(int start, int end);
}
