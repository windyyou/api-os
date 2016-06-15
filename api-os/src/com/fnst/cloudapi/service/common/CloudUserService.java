package com.fnst.cloudapi.service.common;

import java.util.List;

import com.fnst.cloudapi.pojo.common.CloudUser;

public interface CloudUserService extends SuperDaoService<CloudUser,String>{
	public int countNum() throws Exception;
	
	public List<CloudUser> selectList() throws Exception;
	
	public List<CloudUser> selectListForPage(int start,int end)throws Exception;
	
	public CloudUser insertUserAndTenant(CloudUser user) throws Exception;

}
