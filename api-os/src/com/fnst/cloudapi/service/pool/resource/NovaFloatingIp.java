package com.fnst.cloudapi.service.pool.resource;

import java.util.LinkedHashMap;
import java.util.Map;

import com.fnst.cloudapi.service.pool.BasicResource;

public class NovaFloatingIp extends BaseResource {
	private int no;
	private final String type = "OS::Nova::FloatingIP";
	private String pool = null;
	private String resoureName = null;

	public NovaFloatingIp(int no, String pool) {
		this.no = no;
		this.pool = pool;
		this.resoureName = String.format("fip-%03d", this.no);
	}
	
	public NovaFloatingIp(String sName, String pool) {
		this.no = Integer.parseInt(sName.substring(sName.length() - 3, sName.length()));
		this.pool = pool;
		this.resoureName = String.format("fip-%03d", this.no);
	}

	public Map<String, Object> getResourceMap() {
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put("pool", this.pool);
		Map<String, Object> f_ip = new LinkedHashMap<String, Object>();
		f_ip.put("type", this.type);
		f_ip.put("properties", properties);
		Map<String, Object> res = new LinkedHashMap<String, Object>();
		res.put(this.resoureName, f_ip);
		return res;
	}

	@Override
	public String getResourceName() {
		return this.resoureName;
	}

	public static void main(String[] args) {
		NovaFloatingIp fip = new NovaFloatingIp(1, "public");
		Map<String, Object> map = fip.getResourceMap();
		String yaml = BasicResource.convertMAP2YAML(map);
		System.out.println(yaml);
	}

}
