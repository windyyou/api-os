package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.pojo.openstackapi.forgui.PoolEntity;

public interface PoolEntityService {
	public PoolEntity createPoolEntity(Map<String, Object> params, String tokenId);

	public List<PoolEntity> listPoolEntity(String tokenId);

	public PoolEntity getPoolEntityById(String id);

	public void updatePoolEntity(PoolEntity p);
	
	public PoolEntity getPoolEntityByTenantId(String id);
}
