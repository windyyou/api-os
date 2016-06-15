package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.PoolEntity;

public interface PoolEntityMapper {
    int deleteByPrimaryKey(String id);

    int insert(PoolEntity record);

    int insertSelective(PoolEntity record);

    PoolEntity selectByPrimaryKey(String id);
    
    List<PoolEntity> selectByTenantId(String tenantId);

    int updateByPrimaryKeySelective(PoolEntity record);

    int updateByPrimaryKey(PoolEntity record);
}