package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Instance;

public interface InstanceMapper extends SuperMapper<Instance, String> {
	public Integer countNum();

	public Integer countNumByInstanceStatus(String status);
	
	public List<Instance> selectList();

	public List<Instance> selectListForPage(int start, int end);
}
