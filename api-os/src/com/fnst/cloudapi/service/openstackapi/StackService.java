package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Stack;
import com.fnst.cloudapi.pojo.openstackapi.forgui.StackResource;

public interface StackService {
	public List<Stack> getStackList(Map<String, String> paraMap, String tokenId);

	public Stack getStack(String stackId, String tokenId);

	public Stack createStack(String stackName, Map<String, String> paramMap, String template, Map<String, String> files,
			String environment, String tokenId);

	public Stack updateStack(String stackName, Map<String, String> paramMap, String template, Map<String, String> files,
			String environment, String tokenId);

	public List<StackResource> getStackResourceList(String stackName, String stackId, String tokenId);

	public String getStackNameById(String stackId, String tokenId);

	public StackResource getStackResource(String stackName, String stackId, String resourceName, String tokenId);
}
