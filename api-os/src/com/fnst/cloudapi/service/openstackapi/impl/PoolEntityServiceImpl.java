package com.fnst.cloudapi.service.openstackapi.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.dao.common.PoolEntityMapper;
import com.fnst.cloudapi.pojo.openstackapi.forgui.PoolEntity;
import com.fnst.cloudapi.service.openstackapi.PoolEntityService;
import com.fnst.cloudapi.util.UUIDHelper;

@Service
public class PoolEntityServiceImpl implements PoolEntityService {

	@Resource
	PoolEntityMapper poolEntityMapper;

	@SuppressWarnings("unchecked")
	@Override
	public PoolEntity createPoolEntity(Map<String, Object> params, String tokenId) {
		PoolEntity pool = this.initNewPoolEntity(params);
		String poolName = (String) params.get("name");
		pool.setName(poolName);

		// TODO get tenantId from token
		String tenantId = "4446cfc297c949198f1d1b80e123e60f";
		pool.setTenantId(tenantId);

		int tCpus = (Integer) params.get("cpus");
		int tMems = (Integer) params.get("mems");
		pool.settCpus(tCpus);
		pool.settMems(tMems);

		Map<String, Integer> tFipMap = (Map<String, Integer>) params.get("fips");
		String tFips = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			tFips = mapper.writeValueAsString(tFipMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pool.settFips(tFips);

		Map<String, Integer> tVolMap = (Map<String, Integer>) params.get("volumes");
		String tVolumes = null;
		try {
			tVolumes = mapper.writeValueAsString(tVolMap);
		} catch (Exception e) {
			e.printStackTrace();
		}
		pool.settVolumes(tVolumes);

		String dbaas = Boolean.toString((Boolean) params.get("dbaas"));
		String maas = Boolean.toString((Boolean) params.get("maas"));
		String vpnaas = Boolean.toString((Boolean) params.get("vpnaas"));
		String lbaas = Boolean.toString((Boolean) params.get("lbaas"));
		String fwaas = Boolean.toString((Boolean) params.get("fwaas"));
		pool.setDbaas(dbaas);
		pool.setMaas(maas);
		pool.setVpnaas(vpnaas);
		pool.setLbaas(lbaas);
		pool.setFwaas(fwaas);

		this.poolEntityMapper.insert(pool);
		return this.poolEntityMapper.selectByPrimaryKey(pool.getId());
	}

	@SuppressWarnings("unchecked")
	private PoolEntity initNewPoolEntity(Map<String, Object> params) {
		Map<String, Integer> fip = (Map<String, Integer>) params.get("fips");
		Map<String, Integer> ufip = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : fip.entrySet()) {
			ufip.put(entry.getKey(), 0);
		}

		Map<String, Integer> vol = (Map<String, Integer>) params.get("volumes");
		Map<String, Integer> uvol = new LinkedHashMap<>();
		for (Map.Entry<String, Integer> entry : vol.entrySet()) {
			uvol.put(entry.getKey(), 0);
		}

		PoolEntity p = new PoolEntity();
		p.setId(UUIDHelper.buildUUIDStr());
		List<LinkedHashMap<String, String>> stacks = new ArrayList<LinkedHashMap<String, String>>();
		ObjectMapper mapper = new ObjectMapper();
		try {
			p.setuFips(mapper.writeValueAsString(ufip));
			p.setuVolumes(mapper.writeValueAsString(uvol));
			p.setStacks(mapper.writeValueAsString(stacks));
		} catch (Exception e) {
			e.printStackTrace();
		}
		p.setuCpus(0);
		p.setuMems(0);

		return p;
	}

	@Override
	public List<PoolEntity> listPoolEntity(String tokenId) {
		// TODO get tenantId from token
		String tenantId = "4446cfc297c949198f1d1b80e123e60f";
		return this.poolEntityMapper.selectByTenantId(tenantId);
	}

	@Override
	public PoolEntity getPoolEntityById(String id) {
		return this.poolEntityMapper.selectByPrimaryKey(id);
	}

	@Override
	public void updatePoolEntity(PoolEntity p) {
		this.poolEntityMapper.updateByPrimaryKey(p);
	}

	@Override
	public PoolEntity getPoolEntityByTenantId(String id) {
		return this.poolEntityMapper.selectByTenantId(id).get(0);
	}
	

}
