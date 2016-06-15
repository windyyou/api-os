package com.fnst.cloudapi.service.pool.resource;

import java.util.LinkedHashMap;
import java.util.Map;

public class NeutronNet extends BaseResource {
	private String resoureName = null;
	private final String type = "OS::Neutron::Net";
	private String name = null;

	public NeutronNet(String name) {
		this.resoureName = name;
		this.name = name;
	}

	@Override
	public String getResourceName() {
		return this.resoureName;
	}

	@Override
	public Map<String, Object> getResourceMap() {
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put("name", this.name);
		Map<String, Object> net = new LinkedHashMap<String, Object>();
		net.put("type", this.type);
		net.put("properties", properties);
		Map<String, Object> res = new LinkedHashMap<String, Object>();
		res.put(this.resoureName, net);
		return res;
	}

	public static void main(String[] args) {
		NeutronNet net = new NeutronNet("xxnet");
		System.out.println(net.getResourceMap());
	}

}
