package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class PoolEntity {
	private String id;

	private String name;

	private String tenantId;

	private Integer tCpus;

	private Integer tMems;

	private String tFips;

	private String tVolumes;

	private Integer uCpus;

	private Integer uMems;

	private String uFips;

	private String uVolumes;

	private String dbaas;

	private String maas;

	private String vpnaas;

	private String lbaas;

	private String fwaas;

	private String stacks;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id == null ? null : id.trim();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name == null ? null : name.trim();
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId == null ? null : tenantId.trim();
	}

	public Integer gettCpus() {
		return tCpus;
	}

	public void settCpus(Integer tCpus) {
		this.tCpus = tCpus;
	}

	public Integer gettMems() {
		return tMems;
	}

	public void settMems(Integer tMems) {
		this.tMems = tMems;
	}

	public String gettFips() {
		return tFips;
	}

	public void settFips(String tFips) {
		this.tFips = tFips == null ? null : tFips.trim();
	}

	public String gettVolumes() {
		return tVolumes;
	}

	public void settVolumes(String tVolumes) {
		this.tVolumes = tVolumes == null ? null : tVolumes.trim();
	}

	public Integer getuCpus() {
		return uCpus;
	}

	public void setuCpus(Integer uCpus) {
		this.uCpus = uCpus;
	}

	public Integer getuMems() {
		return uMems;
	}

	public void setuMems(Integer uMems) {
		this.uMems = uMems;
	}

	public String getuFips() {
		return uFips;
	}

	public void setuFips(String uFips) {
		this.uFips = uFips == null ? null : uFips.trim();
	}

	public String getuVolumes() {
		return uVolumes;
	}

	public void setuVolumes(String uVolumes) {
		this.uVolumes = uVolumes == null ? null : uVolumes.trim();
	}

	public String getDbaas() {
		return dbaas;
	}

	public void setDbaas(String dbaas) {
		this.dbaas = dbaas == null ? null : dbaas.trim();
	}

	public String getMaas() {
		return maas;
	}

	public void setMaas(String maas) {
		this.maas = maas == null ? null : maas.trim();
	}

	public String getVpnaas() {
		return vpnaas;
	}

	public void setVpnaas(String vpnaas) {
		this.vpnaas = vpnaas == null ? null : vpnaas.trim();
	}

	public String getLbaas() {
		return lbaas;
	}

	public void setLbaas(String lbaas) {
		this.lbaas = lbaas == null ? null : lbaas.trim();
	}

	public String getFwaas() {
		return fwaas;
	}

	public void setFwaas(String fwaas) {
		this.fwaas = fwaas == null ? null : fwaas.trim();
	}

	public String getStacks() {
		return stacks;
	}

	public void setStacks(String stacks) {
		this.stacks = stacks == null ? null : stacks.trim();
	}

	public static String toJSON(PoolEntity p) {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("id", p.getId());
		map.put("name", p.getName());
		map.put("tenant", p.getTenantId());
		map.put("t_cpus", p.gettCpus());
		map.put("t_mems", p.gettMems());
		map.put("u_cpus", p.getuCpus());
		map.put("u_mems", p.getuMems());

		ObjectMapper mapper = new ObjectMapper();
		try {
			Map<String, Object> tVols = mapper.readValue(p.gettVolumes(), new TypeReference<HashMap<String, Object>>() {
			});
			Map<String, Object> tFips = mapper.readValue(p.gettFips(), new TypeReference<HashMap<String, Object>>() {
			});
			Map<String, Object> uVols = mapper.readValue(p.getuVolumes(), new TypeReference<HashMap<String, Object>>() {
			});
			Map<String, Object> uFips = mapper.readValue(p.getuFips(), new TypeReference<HashMap<String, Object>>() {
			});
			map.put("t_fips", tFips);
			map.put("t_volumes", tVols);
			map.put("u_fips", uFips);
			map.put("u_volumes", uVols);
			List<Map<String, String>> stacks = mapper.readValue(p.getStacks(),
					new TypeReference<List<Map<String, String>>>() {
					});
			map.put("stacks", stacks);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		map.put("dbaas", p.getDbaas());
		map.put("maas", p.getMaas());
		map.put("vpnaas", p.getVpnaas());
		map.put("lbaas", p.getLbaas());
		map.put("fwaas", p.getFwaas());
		try {
			return mapper.writeValueAsString(map);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toJSON(List<PoolEntity> pl) {
		StringBuffer sb = new StringBuffer("[");
		for (PoolEntity p : pl) {
			sb.append(PoolEntity.toJSON(p)).append(",");
		}
		return sb.substring(0, sb.length() - 1) + "]";
	}
}