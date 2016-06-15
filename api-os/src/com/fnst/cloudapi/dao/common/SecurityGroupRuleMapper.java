package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroupRule;

public interface SecurityGroupRuleMapper extends SuperMapper<SecurityGroupRule, String> {
	public Integer countNum();
	
	public List<SecurityGroupRule> selectAllList();
	
	public List<SecurityGroupRule> selectListBySecurityGroupId(String securityGroupId);

	public List<SecurityGroupRule> selectListForPage(int start, int end);

}
