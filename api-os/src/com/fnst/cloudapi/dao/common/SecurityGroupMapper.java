package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroup;

public interface SecurityGroupMapper extends SuperMapper<SecurityGroup, String> {
	public Integer countNum();
	
	public List<SecurityGroup> selectAllList();

	public List<SecurityGroup> selectListForPage(int start, int end);
}
