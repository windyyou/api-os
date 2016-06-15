package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class StackResource {
	private String name;
	private String id;
	private String updatedAt;
	private String[] requiredBy;
	private String status;
	private String statusReason;
	private String physicalResourceId;
	private String resourceType;
	private String attributes;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String[] getRequiredBy() {
		return requiredBy;
	}

	public void setRequiredBy(String[] requiredBy) {
		this.requiredBy = requiredBy;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatusReason() {
		return statusReason;
	}

	public void setStatusReason(String statusReason) {
		this.statusReason = statusReason;
	}

	public String getPhysicalResourceId() {
		return physicalResourceId;
	}

	public void setPhysicalResourceId(String physicalResourceId) {
		this.physicalResourceId = physicalResourceId;
	}

	public String getResourceType() {
		return resourceType;
	}

	public void setResourceType(String resourceType) {
		this.resourceType = resourceType;
	}

	public String getAttributes() {
		return attributes;
	}

	public void setAttributes(String attributes) {
		this.attributes = attributes;
	}

	public static String toJSON(StackResource sr) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("id", sr.getPhysicalResourceId());
		map.put("name", sr.getName());

		String attr = sr.getAttributes();
		Map<String, Object> attrMap = null;
		try {
			attrMap = mapper.readValue(attr, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (attrMap.get("status") != null) {
			map.put("status", attrMap.get("status"));
		}
		if (sr.getResourceType().equals("OS::Nova::Server")) {
			map.put("type", "instance");
		} else if (sr.getResourceType().equals("OS::Cinder::Volume")) {
			map.put("type", "volume");
		} else if (sr.getResourceType().equals("OS::Nova::FloatingIP")) {
			map.put("type", "floatingIp");
		} else {
			return null;
		}
		map.put("updateAt", sr.getUpdatedAt());
		map.put("createAt", sr.getUpdatedAt());
		try {
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toJSON(List<StackResource> srl) {
		StringBuffer sb = new StringBuffer("[");
		for (StackResource p : srl) {
			String sp = StackResource.toJSON(p);
			if (sp == null) {
				continue;
			}
			sb.append(sp).append(",");
		}
		return sb.substring(0, sb.length() - 1) + "]";
	}

	public static String toJSON(Stack s, List<StackResource> srl) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("id", s.getId());
		map.put("name", s.getName());
		map.put("description", "template description");
		map.put("status", s.getStatus());
		map.put("createdAt", s.getCreatedAt());
		map.put("updatedAt", s.getUpdatedAt());

		List<Map<String, String>> compute = new ArrayList<Map<String, String>>();
		List<Map<String, String>> storage = new ArrayList<Map<String, String>>();
		List<Map<String, String>> network = new ArrayList<Map<String, String>>();
		for (StackResource sr : srl) {
			if (sr.getResourceType().equals("OS::Nova::Server")) {
				Map<String, String> m = new LinkedHashMap<String, String>();
				m.put("id", sr.getPhysicalResourceId());
				m.put("name", sr.getName());
				m.put("type", "instance");
				m.put("status", getResourceStatus(sr.getAttributes()));
				m.put("createdAt", sr.getUpdatedAt());
				m.put("updatedAt", sr.getUpdatedAt());
				compute.add(m);
			} else if (sr.getResourceType().equals("OS::Cinder::Volume")) {
				Map<String, String> m = new LinkedHashMap<String, String>();
				m.put("id", sr.getPhysicalResourceId());
				m.put("name", sr.getName());
				m.put("status", getResourceStatus(sr.getAttributes()));
				m.put("createdAt", sr.getUpdatedAt());
				m.put("updatedAt", sr.getUpdatedAt());
				m.put("type", "volume");
				storage.add(m);
			} else if (sr.getResourceType().equals("OS::Neutron::Net")) {
				Map<String, String> m = new LinkedHashMap<String, String>();
				m.put("id", sr.getPhysicalResourceId());
				m.put("name", sr.getName());
				m.put("status", getResourceStatus(sr.getAttributes()));
				m.put("createdAt", sr.getUpdatedAt());
				m.put("updatedAt", sr.getUpdatedAt());
				m.put("type", "network");
				network.add(m);
			} else {
				continue;
			}
		}
		map.put("compute", compute);
		map.put("storage", storage);
		map.put("network", network);
		try {
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String getResourceStatus(String attr) {
		ObjectMapper mapper = new ObjectMapper();
		Map<String, Object> attrMap = null;
		try {
			attrMap = mapper.readValue(attr, new TypeReference<Map<String, Object>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (attrMap.get("status") != null) {
			return (String) attrMap.get("status");
		}
		return "";
	}
}
