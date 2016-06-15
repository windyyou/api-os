package com.fnst.cloudapi.service.openstackapi;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Pool;

public interface PoolService {
	public Pool createPool(Pool poolInfo,String tokenId);
}
