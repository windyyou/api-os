package com.fnst.cloudapi.service.pool;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Flavor;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Pool;
import com.fnst.cloudapi.pojo.openstackapi.forgui.PoolEntity;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Stack;
import com.fnst.cloudapi.pojo.openstackapi.forgui.StackResource;
import com.fnst.cloudapi.service.openstackapi.FlavorService;
import com.fnst.cloudapi.service.openstackapi.NetworkService;
import com.fnst.cloudapi.service.openstackapi.PoolEntityService;
import com.fnst.cloudapi.service.openstackapi.StackService;
import com.fnst.cloudapi.service.openstackapi.impl.FlavorServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.NetworkServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.StackServiceImpl;
import com.fnst.cloudapi.service.pool.resource.NeutronNet;
import com.fnst.cloudapi.service.pool.resource.NeutronSubnet;
import com.fnst.cloudapi.service.pool.resource.NovaFloatingIp;
import com.fnst.cloudapi.service.pool.resource.NovaFloatingIpAssociation;
import com.fnst.cloudapi.service.pool.resource.NovaServer;
import com.fnst.cloudapi.util.ParamConstant;

@Service
public class PoolResource implements PoolResourceService {
	@Resource
	PoolEntityService poolEntityService;

	@SuppressWarnings("unchecked")
	public Stack create(Map<String, Object> params, String tokenId) throws Exception {
		// TODO get tenantid from tokenId
		String tenantid = "4446cfc297c949198f1d1b80e123e60f";
		PoolEntity pool = this.poolEntityService.getPoolEntityByTenantId(tenantid);
		if (pool == null) {
			throw new Exception("Pool not exists");
		}

		Map<String, Object> yamlMap = this.initYAML();
		Map<String, Object> resourcesMap = new LinkedHashMap<String, Object>();
		Map<String, String> serverFipRelationship = new LinkedHashMap<String, String>();

		String template_name = this.genStackName(pool);
		ObjectMapper mapper = new ObjectMapper();

		Map<String, Integer> tFips = null;
		Map<String, Integer> tVols = null;
		Map<String, Integer> uFips = null;
		Map<String, Integer> uVols = null;
		try {
			tFips = mapper.readValue(pool.gettFips(), new TypeReference<HashMap<String, Integer>>() {
			});
			tVols = mapper.readValue(pool.gettVolumes(), new TypeReference<HashMap<String, Integer>>() {
			});
			uFips = mapper.readValue(pool.getuFips(), new TypeReference<HashMap<String, Integer>>() {
			});
			uVols = mapper.readValue(pool.getuVolumes(), new TypeReference<HashMap<String, Integer>>() {
			});
		} catch (Exception e) {
			e.printStackTrace();
			throw new Exception("Internal Error");
		}

		int cpusTotal = 0;
		int memsTotal = 0;
		Map<String, Integer> fipTotal = new LinkedHashMap<String, Integer>();
		Map<String, Integer> volTotal = new LinkedHashMap<String, Integer>();

		// *********** NETWORK ***********

		List<Map<String, Object>> netWork = (List<Map<String, Object>>) params.get("network");
		for (Map<String, Object> net : netWork) {
			String name = (String) net.get("name");
			String cidr = (String) net.get("CIDR");
			String ipVirsion = (String) net.get("ipVersion");
			int ipV = 4;
			if (ipVirsion.equals("IP v6")) {
				ipV = 6;
			}
			String gateway = (String) net.get("gateway");
			Boolean dhcp = (Boolean) net.get("DHCP");
			NeutronNet neutronNet = new NeutronNet(name);
			NeutronSubnet neutronSubnet = new NeutronSubnet(name, cidr, gateway, dhcp, ipV);
			resourcesMap.putAll(neutronNet.getResourceMap());
			resourcesMap.putAll(neutronSubnet.getResourceMap());
		}

		// *********** BULK SERVERS ***********
		int count = (int) params.get("quantity");
		String sourceType = (String) params.get("sourceType");
		String image = (String) params.get("source");
		int cpus = (int) params.get("core");
		int mems = (int) params.get("ram");
		int volume_size = (int) params.get("volumeSize");
		String volume_type = (String) params.get("volumeType");

		if (!tVols.containsKey(volume_type)) {
			throw new Exception("VolumeType not exists");
		}
		String flavor = this.getFlavor(tokenId, cpus, mems);

		// TODO get or create
		int i_index = 1;
		List<Map<String, Object>> netInstance = (List<Map<String, Object>>) params.get("netInstance");
		for (Map<String, Object> ni : netInstance) {
			String name = (String) ni.get("name");
			int number = (int) ni.get("number");
			for (int i = 0; i < number; i++) {
				NovaServer server = new NovaServer(i_index, "pool_instance", flavor, name, image,
						Integer.toString(volume_size), "iscsi");
				Map<String, Object> serverMap = server.getResourceMap();
				resourcesMap.putAll(serverMap);
				i_index++;
			}
		}

		cpusTotal += count * cpus;
		memsTotal += count * mems;
		volTotal.put(volume_type, Integer.valueOf(volume_size) * count);

		if (pool.gettCpus() < (pool.getuCpus() + cpusTotal)) {
			throw new Exception("Cpus exceeds pool limit");
		}
		if (pool.gettMems() < (pool.getuMems() + memsTotal)) {
			throw new Exception("Mems exceeds pool limit");
		}

		// *********** SERVER'S FIP ***********
		/*
		 * for (Map.Entry<String, String> entry :
		 * serverFipRelationship.entrySet()) { String sname = entry.getKey();
		 * String fpname = entry.getValue();
		 * 
		 * NovaFloatingIp fip = new NovaFloatingIp(sname, fpname); Map<String,
		 * Object> fipMap = fip.getResourceMap(); resourcesMap.putAll(fipMap);
		 * 
		 * NovaFloatingIpAssociation fipa = new NovaFloatingIpAssociation(sname,
		 * fip.getResourceName()); Map<String, Object> fipaMap =
		 * fipa.getResourceMap(); resourcesMap.putAll(fipaMap);
		 * 
		 * if (!fipTotal.containsKey(fpname)) { fipTotal.put(fpname, 1); } else
		 * { int c = fipTotal.get(fpname); fipTotal.put(fpname, c + 1); } }
		 */

		// *********** update pool ***********
		/*
		 * for (Map.Entry<String, Integer> entry : fipTotal.entrySet()) { String
		 * fipname = entry.getKey(); if ((entry.getValue() + uFips.get(fipname))
		 * > tFips.get(fipname)) { throw new Exception("Fips exceeds pool limit"
		 * ); } uFips.put(fipname, uFips.get(fipname) + entry.getValue()); }
		 */

		for (Map.Entry<String, Integer> entry : volTotal.entrySet()) {
			String volname = entry.getKey();
			if ((entry.getValue() + uVols.get(volname)) > tVols.get(volname)) {
				throw new Exception("Volume exceeds pool limit");
			}
			uVols.put(volname, uVols.get(volname) + entry.getValue());
		}

		pool.setuCpus(pool.getuCpus() + cpusTotal);
		pool.setuMems(pool.getuMems() + memsTotal);
		try {
			// String ufips = mapper.writeValueAsString(uFips);
			String uvols = mapper.writeValueAsString(uVols);
			// pool.setuFips(ufips);
			pool.setuVolumes(uvols);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// *********** BUILD YAML AND CREATE ***********
		yamlMap.put("resources", resourcesMap);
		String yamlTemplate = BasicResource.convertMAP2YAML(yamlMap);
		StackService stackservice = new StackServiceImpl();
		Stack stack = stackservice.createStack(template_name, null, yamlTemplate, null, null, null);
		System.out.println(yamlTemplate);

		if (stack != null) {
			Map<String, String> curStack = new LinkedHashMap<String, String>();
			curStack.put("id", stack.getId());
			curStack.put("name", stack.getName());
			String stacks = null;
			List<Map<String, String>> stackList = null;
			try {
				stackList = mapper.readValue(pool.getStacks(), new TypeReference<List<Map<String, String>>>() {
				});
				stackList.add(curStack);
				stacks = mapper.writeValueAsString(stackList);
			} catch (Exception e) {
				throw new Exception("Internal Error");
			}
			pool.setStacks(stacks);
			this.poolEntityService.updatePoolEntity(pool);

		}
		return stack;
	}

	@Override
	public List<StackResource> getResources(String stackID, String tokenID) throws Exception {
		// TODO get tenantid from tokenId
		String tenantid = "4446cfc297c949198f1d1b80e123e60f";
		PoolEntity pool = this.poolEntityService.getPoolEntityByTenantId(tenantid);
		if (pool == null) {
			throw new Exception("Pool not exists");
		}

		List<Map<String, String>> stackList = null;
		ObjectMapper mapper = new ObjectMapper();
		try {
			stackList = mapper.readValue(pool.getStacks(), new TypeReference<List<Map<String, String>>>() {
			});
		} catch (Exception e) {
			throw new Exception("Internal Error");
		}

		StackService stackService = new StackServiceImpl();
		String stackName = null;
		for (Map<String, String> map : stackList) {
			if (map.get("id").equals(stackID)) {
				stackName = map.get("name");
				break;
			}
		}

		if (stackName == null) {
			throw new Exception("Internal Error");
		}

		List<StackResource> list = stackService.getStackResourceList(stackName, stackID, tokenID);
		for (StackResource s : list) {
			String rname = s.getName();
			StackResource cs = stackService.getStackResource(stackName, stackID, rname, tokenID);
			s.setAttributes(cs.getAttributes());

		}
		return list;
	}

	public String delete(String stackID, String tokenID) {
		return null;
	}

	public String update(Map<String, String> params, String tokenID) {
		return null;
	}

	private Map<String, Object> initYAML() {
		Map<String, Object> map = new LinkedHashMap<String, Object>();
		map.put("heat_template_version", "2013-05-23");
		return map;
	}

	private String genStackName(PoolEntity p) {
		ObjectMapper mapper = new ObjectMapper();
		List<Map<String, String>> stackList = null;
		try {
			stackList = mapper.readValue(p.getStacks(), new TypeReference<List<Map<String, String>>>() {
			});
			return "template_" + (stackList.size() + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private String getFlavor(String guiToken, int flavor_vcpus, int flavor_ram) {
		int vcpus = flavor_vcpus;
		int ram = flavor_ram;
		FlavorService flavorService = new FlavorServiceImpl();
		List<Flavor> list = flavorService.getFlavorList(null, guiToken);
		if (null != list) {
			for (Flavor flavor : list) {
				if (flavor.getRam() == ram && flavor.getVcpus() == vcpus) {
					return flavor.getId();
				}
			}
		}
		Map<String, String> flavorParamMap = new HashMap<String, String>();
		String name = String.format("%s_%s_Flavor", vcpus, ram);
		flavorParamMap.put(ParamConstant.NAME, name);
		flavorParamMap.put(ParamConstant.RAM, Integer.toString(ram));
		flavorParamMap.put(ParamConstant.VCPUS, Integer.toString(vcpus));
		flavorParamMap.put(ParamConstant.DISK, Integer.toString(100));
		Flavor decidedFlavor = flavorService.createFlavor(flavorParamMap, guiToken);
		if (null == decidedFlavor)
			return null;

		// TODO save the decidedFlavor to the local db
		return decidedFlavor.getId();
	}

	public static void main(String[] args) {
		// PoolResourceService pr = new PoolResourceServiceImpl();

		Map<String, Object> paramMap = new LinkedHashMap<String, Object>();
		Map<String, String> serverParams = new LinkedHashMap<String, String>();
		serverParams.put("count", "2");
		serverParams.put("cores", "1");
		serverParams.put("rams", "128");
		serverParams.put("image", "22cf53ef-82a8-4603-b203-ebb0bdedb7e3");
		serverParams.put("volume_size", "1");
		serverParams.put("volume_type", "iscsi");
		serverParams.put("fip_pool", "public");
		paramMap.put("bulk_servers", serverParams);

		// Map<String, String> fipParams = new LinkedHashMap<String, String>();
		// fipParams.put("count", "1");
		// fipParams.put("pool", "public");
		// paramMap.put("fip", fipParams);
		try {
			// pr.create(paramMap, "");
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}

class RefreshStackStatusThread implements Runnable {
	private String stackId;
	private String tokenId;
	private Pool pool;
	private Flavor flavor;

	public RefreshStackStatusThread(Pool pool, Flavor flavor, String tokenId) {
		this.stackId = stackId;
		this.tokenId = tokenId;
		this.pool = pool;
		this.flavor = flavor;
	}

	@Override
	public void run() {
		StackService stackService = new StackServiceImpl();
		while (true) {
			Stack stack = null;
			try {
				Thread.sleep(3000);
				stack = stackService.getStack(this.stackId, tokenId);
			} catch (Exception e) {
				continue;
			}
			String status = stack.getStatus();
			if (status.equals("CREATE_COMPLETE")) {
				System.out.println("***** STACK: " + this.stackId + " CREATE_COMPLETE *****");
				stack.setStatus("CREATE_COMPLETE");
				// TODO save stack to db
				break;
			}
			if (status.equals("CREATE_FAILED")) {
				System.out.println("***** STACK: " + this.stackId + " CREATE_FAILED *****");
				stack.setStatus("CREATE_FAILED");
				// TODO save stack to db
				// TODO rollback pool resource
				int cpu = this.flavor.getVcpus();
				int ram = this.flavor.getRam();
				// pool.setCores(this.pool.getCores() - cpu);
				// pool.setRamsSize(this.pool.getRamSize() - ram);
				break;
			}
			if (status.equals("CREATE_IN_PROGRESS")) {
				System.out.println("***** STACK: " + this.stackId + " CREATE_IN_PROGRESS *****");
				continue;
			}
		}
	}
}
