package com.fnst.cloudapi.service.pool;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Stack;
import com.fnst.cloudapi.service.openstackapi.StackService;
import com.fnst.cloudapi.service.openstackapi.impl.StackServiceImpl;
import com.fnst.cloudapi.service.pool.resource.InstanceResource;

public abstract class BasicResource {
	public enum ResourceAction {
		CREATE, UPDATE, DELETE, ROLLBACK
	}

	public String getTemplateContent(String template, boolean json) {
		String path = "src/com/fnst/cloudapi/service/pool/resource/template/" + template;
		if (json) {
			path = path + ".json";
		} else {
			path = path + ".yaml";
		}
		File t_file = new File(path);
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(t_file));
			StringBuffer sb = new StringBuffer();
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				if (json) {
					sb.append(temp);
				} else {
					sb.append(temp + "\n");
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return null;
	}

	public String convertYAML2JSON(String yaml) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			System.out.println("yaml: " + yaml);
			JsonNode rootNode = mapper.readTree(yaml);
			// System.out.println("all: " + rootNode.toString());
			// String resources =
			// rootNode.path("heat_template_version").textValue();
			// System.out.println("all: " + resources);
			return rootNode.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

	public static String convertMAP2YAML(Map<String, Object> map) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			String yaml = mapper.writeValueAsString(map);
			return yaml.substring(4, yaml.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String convertMAP2JSON(Map<String, Object> map) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String addResource2YAML(String yaml, String resName, Map<String, Object> resMap) {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			Map<String, Object> map = mapper.readValue(yaml, new TypeReference<HashMap<String, Object>>() {
			});
			@SuppressWarnings("unchecked")
			Map<String, Object> resources = (Map<String, Object>) map.get("resources");
			resources.put(resName, resMap);
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updatePool(ResourceAction action) {
		switch (action) {
		case CREATE:
			break;
		case UPDATE:
			break;
		case DELETE:
			break;
		case ROLLBACK:
			break;
		}
	}

	@SuppressWarnings("unchecked")
	public static void main(String[] args) {
		InstanceResource ir = new InstanceResource();
		String t = ir.getTemplateContent(ir.getTemplate(), false);
		System.out.println("template: " + t);
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			JsonNode rootNode = mapper.readTree(t);
			// System.out.println(rootNode.get("outputs").get("server_name"));
			Map<String, Object> map = mapper.readValue(t, new TypeReference<HashMap<String, Object>>() {
			});
			// System.out.println(map.get("outputs"));
			// System.out.println("xxx" + ((Map<String, Object>)
			// map.get("outputs")).get("server_name"));
			// String des = (String) ((Map<String, Object>)((Map<String,
			// Object>)
			// map.get("outputs")).get("server_name")).get("description");
			Map<String, Object> resources = (Map<String, Object>) map.get("resources");
			// System.out.println(resources);
			// resources.put("vm22", "xxxxxxxxxx");
			// System.out.println(map);
			String write = mapper.writeValueAsString(map);
			System.out.println("FINAL:" + write);

			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put("image_id", "22cf53ef-82a8-4603-b203-ebb0bdedb7e3");
			paramMap.put("flavor", "tiny");
			paramMap.put("network", "net1");
			paramMap.put("server_name", "template_test");
			StackService stackService = new StackServiceImpl();
			Stack stack = stackService.createStack("xxxx", paramMap, write, null, null, "");

		} catch (Exception e) {

		}

	}
}
