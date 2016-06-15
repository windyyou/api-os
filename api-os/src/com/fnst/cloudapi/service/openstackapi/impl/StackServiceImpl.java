package com.fnst.cloudapi.service.openstackapi.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Stack;
import com.fnst.cloudapi.pojo.openstackapi.forgui.StackResource;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.StackService;
import com.fnst.cloudapi.service.pool.BasicResource;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class StackServiceImpl extends CloudConfigAndTokenHandler implements StackService {
	private HttpClientForOsRequest client = null;
	private int NORMAL_RESPONSE_CODE = 201;

	public StackServiceImpl() {
		this.client = new HttpClientForOsRequest();
	}

	@Override
	public List<Stack> getStackList(Map<String, String> paramMap, String tokenId) {
		TokenOs ot = super.osToken;
		String region = "RegionOne";
		String url = ot.getEndPoint(TokenOs.EP_TYPE_ORCHESTRATION, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/stacks", paramMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		int resCode = Integer.parseInt(rs.get("httpcode"));
		String resBody = rs.get("jsonbody");
		System.out.println("【CODE】:  " + resCode);
		System.out.println("【BODY】:  " + resBody);

		List<Stack> list = null;
		if (resCode > 400) {
			System.out.println("List Stacks failed");
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(resBody);
				JsonNode stacksNode = rootNode.path("stacks");
				int stacks_num = stacksNode.size();
				if (stacks_num > 0) {
					list = new ArrayList<Stack>();
					for (int i = 0; i < stacks_num; i++) {
						Stack one = new Stack();
						JsonNode oneStack = stacksNode.get(i);
						String id = oneStack.path("id").textValue();
						one.setId(id);
						one.setName(oneStack.path("stack_name").textValue());
						one.setStatus(oneStack.path("stack_status").textValue());
						one.setStatusReason(oneStack.path("stack_status_reason").textValue());
						one.setCreatedAt(oneStack.path("creation_time").textValue());
						one.setUpdatedAt(oneStack.path("updated_time").textValue());
						one.setOwner(oneStack.path("stack_owner").textValue());
						list.add(one);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public Stack createStack(String stackName, Map<String, String> paramMap, String template, Map<String, String> files,
			String environment, String tokenId) {
		TokenOs ot = super.osToken;
		String region = "RegionOne";
		String url = ot.getEndPoint(TokenOs.EP_TYPE_ORCHESTRATION, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/stacks", paramMap);
		Map<String, Object> body = new HashMap<String, Object>();
		if (files == null) {
			body.put("files", new HashMap<String, String>());
		} else {
			body.put("files", files);
		}
		if (environment == null) {
			body.put("environment", new HashMap<String, String>());
		} else {
			body.put("environment", environment);
		}
		body.put("disable_rollback", false);
		body.put("stack_name", stackName);
		if (paramMap == null) {
			body.put("parameters", new HashMap<String, String>());
		} else {
			body.put("parameters", paramMap);
		}
		body.put("template", template);

		String bodyStr = BasicResource.convertMAP2JSON(body);

		System.out.println("【BODYSTRING】: " + bodyStr);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
		headers.put("Content-Type", "application/json");

		Map<String, String> rs = client.httpDoPost(url, headers, bodyStr);

		Stack stack = null;
		int resCode = Integer.parseInt(rs.get("httpcode"));
		String resBody = rs.get("jsonbody");
		System.out.println("【CODE】:  " + resCode);
		System.out.println("【BODY】:  " + resBody);

		if (resCode != NORMAL_RESPONSE_CODE) {
			System.out.println("Create Stack failed");
			return null;
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(resBody);
				JsonNode stackNode = rootNode.path("stack");
				String id = stackNode.path("id").textValue();
				stack = new Stack();
				stack.setId(id);
				stack.setName(stackName);
				stack.setStatus("CREAT_IN_PROGRESS");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return stack;
	}

	@Override
	public Stack updateStack(String stackName, Map<String, String> paramMap, String template, Map<String, String> files,
			String environment, String tokenId) {
		TokenOs ot = super.osToken;
		String region = "RegionOne";
		String url = ot.getEndPoint(TokenOs.EP_TYPE_ORCHESTRATION, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/stacks", paramMap);
		Map<String, Object> body = new HashMap<String, Object>();
		if (files == null) {
			body.put("files", new HashMap<String, String>());
		} else {
			body.put("files", files);
		}
		if (environment == null) {
			body.put("environment", new HashMap<String, String>());
		} else {
			body.put("environment", environment);
		}
		body.put("disable_rollback", false);
		body.put("stack_name", stackName);
		if (paramMap == null) {
			body.put("parameters", new HashMap<String, String>());
		} else {
			body.put("parameters", paramMap);
		}
		body.put("template", template);

		String bodyStr = BasicResource.convertMAP2JSON(body);

		System.out.println("【BODYSTRING】: " + bodyStr);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());
		headers.put("Content-Type", "application/json");

		Map<String, String> rs = client.httpDoPut(url, headers, bodyStr);

		Stack stack = null;
		int resCode = Integer.parseInt(rs.get("httpcode"));
		String resBody = rs.get("jsonbody");
		System.out.println("【CODE】:  " + resCode);
		System.out.println("【BODY】:  " + resBody);

		if (resCode != NORMAL_RESPONSE_CODE) {
			System.out.println("Create Stack failed");
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(resBody);
				JsonNode stackNode = rootNode.path("stack");
				String id = stackNode.path("id").textValue();
				stack = new Stack();
				stack.setId(id);
				stack.setStatus("CREATING");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return stack;
	}

	@Override
	public Stack getStack(String stackId, String tokenId) {
		TokenOs ot = super.osToken;
		String region = "RegionOne";
		String url = ot.getEndPoint(TokenOs.EP_TYPE_ORCHESTRATION, region).getPublicURL();
		url = url + "/stacks/" + stackId;

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		int resCode = Integer.parseInt(rs.get("httpcode"));
		String resBody = rs.get("jsonbody");
		System.out.println("【CODE】:  " + resCode);
		System.out.println("【BODY】:  " + resBody);

		Stack stack = null;
		if (resCode > 400) {
			System.out.println("Get Stack failed");
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(resBody);
				JsonNode stackNode = rootNode.path("stack");
				String id = stackNode.path("id").textValue();
				stack = new Stack();
				stack.setId(id);
				stack.setName(stackNode.path("stack_name").textValue());
				stack.setStatus(stackNode.path("stack_status").textValue());
				stack.setStatusReason(stackNode.path("stack_status_reason").textValue());
				stack.setCreatedAt(stackNode.path("creation_time").textValue());
				stack.setUpdatedAt(stackNode.path("updated_time").textValue());
				stack.setOwner(stackNode.path("stack_owner").textValue());
				JsonNode outNodes = stackNode.path("outputs");
				int out_num = outNodes.size();
				if (out_num > 0) {
					for (int i = 0; i < out_num; i++) {
						JsonNode curNode = outNodes.get(i);
						if (curNode.path("output_key").textValue().equals("template_name")) {
							stack.setTemplate(curNode.path("output_value").textValue());
							break;
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return stack;
	}

	@Override
	public List<StackResource> getStackResourceList(String stackName, String stackId, String tokenId) {
		TokenOs ot = super.osToken;
		String region = "RegionOne";
		String url = ot.getEndPoint(TokenOs.EP_TYPE_ORCHESTRATION, region).getPublicURL();
		url = url + "/stacks/" + stackName + "/" + stackId + "/resources";

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		int resCode = Integer.parseInt(rs.get("httpcode"));
		String resBody = rs.get("jsonbody");
		System.out.println("【CODE】:  " + resCode);
		System.out.println("【BODY】:  " + resBody);

		List<StackResource> list = null;
		if (resCode > 400) {
			System.out.println("List Stack's Resources failed");
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(resBody);
				JsonNode resourcesNode = rootNode.path("resources");
				int stacks_num = resourcesNode.size();
				if (stacks_num > 0) {
					list = new ArrayList<StackResource>();
					for (int i = 0; i < stacks_num; i++) {
						StackResource one = new StackResource();
						JsonNode oneResource = resourcesNode.get(i);
						String id = oneResource.path("logical_resource_id").textValue();
						one.setId(id);
						one.setName(oneResource.path("resource_name").textValue());
						one.setPhysicalResourceId(oneResource.path("physical_resource_id").textValue());

						JsonNode requiredByNode = oneResource.path("required_by");
						String[] rb = new String[requiredByNode.size()];
						for (int a = 0; a < requiredByNode.size(); a++) {
							rb[a] = requiredByNode.get(a).textValue();
						}
						one.setRequiredBy(rb);

						one.setResourceType(oneResource.path("resource_type").textValue());
						one.setStatus(oneResource.path("resource_status").textValue());
						one.setStatusReason(oneResource.path("resource_status_reason").textValue());
						one.setUpdatedAt(oneResource.path("updated_time").textValue());
						list.add(one);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	@Override
	public StackResource getStackResource(String stackName, String stackId, String resourceName, String tokenId) {
		TokenOs ot = super.osToken;
		String region = "RegionOne";
		String url = ot.getEndPoint(TokenOs.EP_TYPE_ORCHESTRATION, region).getPublicURL();
		url = url + "/stacks/" + stackName + "/" + stackId + "/resources/" + resourceName;

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put("X-Auth-Token", ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		int resCode = Integer.parseInt(rs.get("httpcode"));
		String resBody = rs.get("jsonbody");
		System.out.println("【CODE】:  " + resCode);
		System.out.println("【BODY】:  " + resBody);

		StackResource one = null;
		if (resCode > 400) {
			System.out.println("Get Stack failed");
		} else {
			try {
				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(resBody);
				JsonNode oneResource = rootNode.path("resource");
				one = new StackResource();
				String id = oneResource.path("logical_resource_id").textValue();
				one.setId(id);
				one.setName(oneResource.path("resource_name").textValue());
				one.setPhysicalResourceId(oneResource.path("physical_resource_id").textValue());

				JsonNode requiredByNode = oneResource.path("required_by");
				String[] rb = new String[requiredByNode.size()];
				for (int a = 0; a < requiredByNode.size(); a++) {
					rb[a] = requiredByNode.get(a).textValue();
				}
				one.setRequiredBy(rb);

				one.setResourceType(oneResource.path("resource_type").textValue());
				one.setStatus(oneResource.path("resource_status").textValue());
				one.setStatusReason(oneResource.path("resource_status_reason").textValue());
				one.setUpdatedAt(oneResource.path("updated_time").textValue());
				one.setAttributes(oneResource.path("attributes").toString());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return one;
	}

	@Override
	public String getStackNameById(String stackId, String tokenId) {
		List<Stack> stackList = this.getStackList(null, tokenId);
		for (Stack s : stackList) {
			if (s.getId().equals(stackId)) {
				return s.getName();
			}
		}
		return null;
	}

	public static void main(String[] args) {
		StackServiceImpl stackService = new StackServiceImpl();
		// List<Stack> list = stackService.getStackList(new HashMap<String,
		// String>(), "");

		// InstanceResource ir = new InstanceResource();
		// String template = ir.getTemplateContent(ir.getTemplate());
		// Map<String, String> paramMap = new HashMap<String, String>();
		// paramMap.put("image_id", "22cf53ef-82a8-4603-b203-ebb0bdedb7e3");
		// paramMap.put("flavor", "tiny");
		// paramMap.put("network", "net1");
		// paramMap.put("server_name", "template_test");
		// Stack stack = stackService.createStack("oxox", paramMap, template,
		// "");
		// Stack stack =
		// stackService.getStack("82fc60be-7536-4301-bfe4-5f4b213442c3", "");
		// List<StackResource> resourceList =
		// stackService.getStackResourceList("xxxooo",
		// "55c2f1db-88de-448a-b883-2209fbef93c0", "");
		StackResource r = stackService.getStackResource("xxxxiii", "044c890a-def8-4e48-8de7-59cadf928841", "fip-001",
				"");
		System.out.println();
	}

}
