package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InstanceConfig {
	private Map<String, Object> core = new HashMap<>();
	private Map<String, Object> ram = new HashMap<>();
	private Map<String, Object> volume = new HashMap<>();
	private List<Object> instanceType = new ArrayList<>();

	public Map<String, Object> getCore() {
		return core;
	}

	public void setCore(Map<String, Object> core) {
		this.core = core;
	}

	public Map<String, Object> getRam() {
		return ram;
	}

	public void setRam(Map<String, Object> ram) {
		this.ram = ram;
	}

	public Map<String, Object> getVolume() {
		return volume;
	}

	public void setVolume(Map<String, Object> volume) {
		this.volume = volume;
	}

	public List<Object> getInstanceType() {
		return instanceType;
	}

	public void setInstanceType(List<Object> instanceType) {
		this.instanceType = instanceType;
	}

}
