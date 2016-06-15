package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.common.CloudUser;

public interface CloudUserMapper extends SuperMapper<CloudUser,String>{
	
	public int countNum();
	
	public int countNumByUserCode(String code);
	
	public  List<CloudUser> selectList();
	
	public List<CloudUser> selectListForPage(int start,int end);
	
}
