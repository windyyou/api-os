package com.fnst.cloudapi.service.pool;

import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Stack;
import com.fnst.cloudapi.pojo.openstackapi.forgui.StackResource;

public interface PoolResourceService {
	public Stack create(Map<String, Object> params, String tokenId) throws Exception;

	public List<StackResource> getResources(String stackId, String tokenId) throws Exception;
}
