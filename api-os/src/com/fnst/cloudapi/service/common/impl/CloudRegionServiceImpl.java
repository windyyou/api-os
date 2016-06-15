package com.fnst.cloudapi.service.common.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fnst.cloudapi.dao.common.RegionMapper;
import com.fnst.cloudapi.dao.common.SuperMapper;
import com.fnst.cloudapi.pojo.common.Region;
import com.fnst.cloudapi.service.common.CloudRegionService;

@Service
public class CloudRegionServiceImpl extends SuperDaoServiceImpl<Region, String> implements CloudRegionService{
    @Resource
    RegionMapper regionMapper;
    
	@Override
	public int countNum() throws Exception {
		// TODO Auto-generated method stub
		return regionMapper.countNum();
	}

	@Override
	public List<Region> selectList() throws Exception {
		// TODO Auto-generated method stub
		return regionMapper.selectList();
	}

	@Override
	public SuperMapper<Region, String> getMapper() {
		// TODO Auto-generated method stub
		return this.regionMapper;
	}

}
