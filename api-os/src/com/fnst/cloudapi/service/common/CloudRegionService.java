package com.fnst.cloudapi.service.common;

import java.util.List;

import com.fnst.cloudapi.pojo.common.Region;

public interface CloudRegionService extends SuperDaoService<Region,String>{
	
	public int countNum()throws Exception;
	public List<Region> selectList()throws Exception;

}
