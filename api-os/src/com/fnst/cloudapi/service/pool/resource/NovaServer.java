package com.fnst.cloudapi.service.pool.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

public class NovaServer extends BaseResource {
	private int no;
	private final String type = "OS::Nova::Server";
	private String name = null;
	private String flavor = null;
	private List<Map<String, Object>> block_device_mapping_v2 = null;
	private List<Map<String, String>> networks = null;
	private String image = null;
	private String volume_size = null;
	private String volume_type = null;
	private String resoureName = null;
	private String sysvol_res_name = null;
	private String netResourceName = null;

	public NovaServer(int no, String name, String flavor, String[] nets, String image, String volume_size,
			String volume_type) {
		this.no = no;
		this.name = name + String.format("_%03d", this.no);
		this.flavor = flavor;
		this.setNetworks(nets);
		this.image = image;
		this.volume_size = volume_size;
		this.volume_type = volume_type;
		this.resoureName = String.format("server-%03d", this.no);
		this.sysvol_res_name = String.format("sysvol-%03d", this.no);
		this.setBDMv2(sysvol_res_name);
	}

	public NovaServer(int no, String name, String flavor, String netResourceName, String image, String volume_size,
			String volume_type) {
		this.no = no;
		this.name = name + String.format("_%03d", this.no);
		this.flavor = flavor;
		this.netResourceName = netResourceName;
		this.image = image;
		this.volume_size = volume_size;
		this.volume_type = volume_type;
		this.resoureName = String.format("server-%03d", this.no);
		this.sysvol_res_name = String.format("sysvol-%03d", this.no);
		this.setBDMv2(sysvol_res_name);
	}

	@Override
	public String getResourceName() {
		return this.resoureName;
	}

	private void setBDMv2(String vol_res) {
		Map<String, Object> bdm = new HashMap<String, Object>();
		Map<String, String> vid = new HashMap<String, String>();
		vid.put("get_resource", vol_res);
		bdm.put("volume_id", vid);
		bdm.put("device_type", "disk");
		bdm.put("boot_index", "0");
		bdm.put("delete_on_termination", "true");
		List<Map<String, Object>> bdmv2 = new ArrayList<Map<String, Object>>();
		bdmv2.add(bdm);
		this.block_device_mapping_v2 = bdmv2;
	}

	private void setNetworks(String[] nets) {
		List<Map<String, String>> networks = new ArrayList<Map<String, String>>();
		for (String net : nets) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("network", net);
			networks.add(map);
		}
		this.networks = networks;
	}

	@Override
	public Map<String, Object> getResourceMap() {
		Map<String, Object> properties = new LinkedHashMap<String, Object>();
		properties.put("name", this.name);
		properties.put("flavor", this.flavor);
		properties.put("block_device_mapping_v2", this.block_device_mapping_v2);
		if (netResourceName != null) {
			List<Map<String, Object>> ll = new ArrayList<Map<String, Object>>();
			Map<String, String> r1 = new LinkedHashMap<String, String>();
			r1.put("get_resource", this.netResourceName);
			Map<String, Object> r2 = new LinkedHashMap<String, Object>();
			r2.put("network", r1);
			ll.add(r2);
			properties.put("networks", ll);
		} else if (this.networks != null) {
			properties.put("networks", this.networks);
		}
		Map<String, Object> server = new LinkedHashMap<String, Object>();
		server.put("type", this.type);
		server.put("properties", properties);

		Map<String, Object> properties_v = new LinkedHashMap<String, Object>();
		properties_v.put("image", this.image);
		properties_v.put("size", this.volume_size);
		properties_v.put("volume_type", this.volume_type);

		Map<String, Object> vol = new LinkedHashMap<String, Object>();
		vol.put("type", "OS::Cinder::Volume");
		vol.put("properties", properties_v);

		Map<String, Object> res = new LinkedHashMap<String, Object>();

		res.put(this.resoureName, server);
		res.put(this.sysvol_res_name, vol);
		return res;
	}

	public static void main(String[] args) {
		NovaServer ns = new NovaServer(1, "xxserver", "tiny", new String[] { "net1" },
				"22cf53ef-82a8-4603-b203-ebb0bdedb7e3", "1", "iscsi");
		Map<String, Object> map1 = ns.getResourceMap();
		System.out.println(map1);

		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		try {
			System.out.println(mapper.writeValueAsString(map1));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
