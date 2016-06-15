package com.fnst.cloudapi.service.openstackapi.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.FloatingIP;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Host;
import com.fnst.cloudapi.pojo.openstackapi.forgui.HostState;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Pool;
import com.fnst.cloudapi.pojo.openstackapi.forgui.StorageQuota;
import com.fnst.cloudapi.pojo.openstackapi.forgui.VolumeType;
import com.fnst.cloudapi.service.openstackapi.FloatingIPService;
import com.fnst.cloudapi.service.openstackapi.HostService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.PoolService;
import com.fnst.cloudapi.service.openstackapi.QuotaService;
import com.fnst.cloudapi.service.openstackapi.VolumeService;
import com.fnst.cloudapi.util.ParamConstant;

public class PoolServiceImpl implements PoolService {

	class HardSpecInfo {
		private int totalCores;
		private int totalRam;
		private Map<String, Float> totalDiskInfo;

		public void setTotalCores(int totalCores) {
			this.totalCores = totalCores;
		}

		public int getTotalCores() {
			return this.totalCores;
		}

		public void setTotalRam(int totalRam) {
			this.totalRam = totalRam;
		}

		public int getTotalRam() {
			return this.totalRam;
		}

		public void addDiskInfo(String diskType, float capacity) {
			if (null == this.totalDiskInfo)
				this.totalDiskInfo = new HashMap<String, Float>();
			this.totalDiskInfo.put(diskType, capacity);
		}

		public Map<String, Float> getDiskInfo() {
			return this.totalDiskInfo;
		}
	}

	private HardSpecInfo harsSpecInfo = null;

	@Override
	public Pool createPool(Pool poolInfo, String tokenId) {

		try {
			if (false == checkPoolResourceQuota(poolInfo, tokenId))
				return null;
		} catch (BusinessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // TODO emit the error message

		// TODO
		// createPool() //create pool and save it to database

		return null;
	}

	private Boolean checkPoolResourceQuota(Pool pool, String adminTokenID) throws BusinessException {
		if (null == pool)
			return false;
		if ("".equals(pool.getName()))
			return false;
		if (pool.getCores() <= 0 || pool.getRamSize() <= 0)
			return false;
		Map<String, Integer> diskInfo = pool.getDiskInfo();
		if (diskInfo.get(ParamConstant.PERFORMANCE_DISK).intValue() <= 0
				&& diskInfo.get(ParamConstant.HIGH_PERFORMANCE_DISK).intValue() <= 0
				&& diskInfo.get(ParamConstant.CAPACITY_DISK).intValue() <= 0)
			return false;

		/*
		 * the logic to check the pool quota Example cpu check: free cores of
		 * hosts: 100,allocated cores of others tenants: 80,requested cores of
		 * pool: 40 40 > (100-80),failed free cores of hosts: 100,allocated
		 * cores of others tenants: 40,requested cores of pool: 40 40 <
		 * (100-40),successed
		 */
		// check vcpus,memory,disk
		if (false == checkHardSpec(pool, adminTokenID))
			return false;

		// check floatingip
		if (false == checkFloatingIP(pool, adminTokenID))
			return false;

		return true;
	}

	private Boolean checkHardSpec(Pool pool, String adminTokenID) throws BusinessException {
		// TODO first,get the total cores and ram from database
		// if not, get the total cores from openstack
		HardSpecInfo totalHardSpecInfo = setHardSpecInfo(adminTokenID);

		// get the used cores and ram from database
		HardSpecInfo usedHardSpecInfo = new HardSpecInfo();

		// check memory and vcpu
		if (pool.getCores() > (totalHardSpecInfo.getTotalCores() - usedHardSpecInfo.getTotalCores()))
			return false;
		if (pool.getRamSize() * ParamConstant.MB > (totalHardSpecInfo.getTotalRam() - usedHardSpecInfo.getTotalRam()))
			return false;

		return true;
	}

	private Boolean checkFloatingIP(Pool pool, String adminTokenID) throws BusinessException {
		// TODO first,get the total floating ip from database
		// if not,get the total floating ip from openstack
		Map<String, Integer> totalFloatingIP = getFloatingIPs(adminTokenID);

		// TODO get the used floating ip from database
		Map<String, Integer> usedFloatingIP = getusedFloatingIP();// new
																	// HashMap<String,Integer>();

		// get the free floating ip from database
		Map<String, Integer> freeFloatingIP = new HashMap<String, Integer>();
		freeFloatingIP.put(ParamConstant.BGP_NETWORK,
				totalFloatingIP.get(ParamConstant.BGP_NETWORK) - usedFloatingIP.get(ParamConstant.BGP_NETWORK));
		freeFloatingIP.put(ParamConstant.TELECOM_NETWORK,
				totalFloatingIP.get(ParamConstant.TELECOM_NETWORK) - usedFloatingIP.get(ParamConstant.TELECOM_NETWORK));
		freeFloatingIP.put(ParamConstant.UNICOM_NETWORK,
				totalFloatingIP.get(ParamConstant.UNICOM_NETWORK) - usedFloatingIP.get(ParamConstant.UNICOM_NETWORK));
		freeFloatingIP.put(ParamConstant.MOBILE_NETWORK,
				totalFloatingIP.get(ParamConstant.MOBILE_NETWORK) - usedFloatingIP.get(ParamConstant.MOBILE_NETWORK));

		for (Map.Entry<String, Integer> entry : pool.getFloatingIPNumbers().entrySet()) {
			if (entry.getValue() > freeFloatingIP.get(entry.getKey()))
				return false;
		}
		return true;
	}

	// get the cpu info and memory info of all compute hosts
	private List<Host> getHostsDetails(String adminTokenId) {
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamConstant.SERVICE_NAME, TokenOs.EP_TYPE_COMPUTE);
		HostService resService = OsApiServiceFactory.getHostService();
		List<Host> hosts = resService.getHostList(paramMap, adminTokenId);
		if (null == hosts || 0 == hosts.size())
			return null;
		List<Host> hostsDetails = new ArrayList<Host>();
		for (Host host : hosts) {
			Host detail = resService.getHostDetails(host.getHostName(), adminTokenId);
			if (null == detail)
				continue;
			hostsDetails.add(detail);
		}
		return hostsDetails;
	}

	// get the floating info of all compute hosts
	private Map<String, Integer> getFloatingIPs(String adminTokenId) throws BusinessException {

		FloatingIPService floatingIPService = OsApiServiceFactory.getFloatingIPService();
		List<FloatingIP> floatingIPs = floatingIPService.getFloatingIPList(null, adminTokenId,null);
		if (null == floatingIPs || 0 == floatingIPs.size())
			return null;
		Map<String, Integer> floatingIpMap = new HashMap<String, Integer>();

		List<FloatingIP> floatingIPDetails = new ArrayList<FloatingIP>();
		for (FloatingIP floatingIP : floatingIPDetails) {
			if (floatingIpMap.containsKey(floatingIP.getNetworkId()))
				floatingIpMap.put(floatingIP.getNetworkId(), floatingIpMap.get(floatingIP.getNetworkId()) + 1);
			else
				floatingIpMap.put(floatingIP.getNetworkId(), 1);
		}
		// TODO sava the floatingip size to database
		return floatingIpMap;
	}

	private HardSpecInfo setHardSpecInfo(String adminTokenID) throws BusinessException {
		// TODO first,get the total cores and ram from database

		// it should be called only once
		List<Host> hosts = getHostsDetails(adminTokenID);
		if (null == hosts || 0 == hosts.size())
			return null;
		// if(null == PoolController.harsSpecInfo)
		// PoolController.harsSpecInfo = new HardSpecInfo();
		this.harsSpecInfo = new HardSpecInfo();
		int totalCores = 0;
		int totalRam = 0; // MB
		for (Host host : hosts) {
			Map<Integer, HostState> hostState = host.getHostsState();
			totalCores += (hostState.get(ParamConstant.TOTAL_RES).getVcpus()
					- hostState.get(ParamConstant.FREE_RES).getVcpus());
			totalRam += (hostState.get(ParamConstant.TOTAL_RES).getRamSize()
					- hostState.get(ParamConstant.FREE_RES).getRamSize());
			// usedCores += hostState.get(ParamConstant.USED_RES).getVcpus();
			// usedRam += hostState.get(ParamConstant.USED_RES).getRamSize();
		}
		this.harsSpecInfo.setTotalRam(totalRam);
		this.harsSpecInfo.setTotalCores(totalCores);

		setStorageSpecInfo(adminTokenID);

		// PoolController.harsSpecInfo.setUsedCores(usedCores);
		// PoolController.harsSpecInfo.setUsedRam(usedRam);
		// TODO set the totalCores、totalRam、totalDiskCapacity to database
		return this.harsSpecInfo;
	}

	private void setStorageSpecInfo(String adminTokenID) throws BusinessException {
		QuotaService quotaresService = OsApiServiceFactory.getQuotaService();
		List<StorageQuota> storagesQuota = quotaresService.getStorageQuotas(null, adminTokenID);

		VolumeService volumeresService = OsApiServiceFactory.getVolumeService();
		List<VolumeType> volumesType = volumeresService.getVolumeTypeList(null, adminTokenID,null);

		for (VolumeType volumeType : volumesType) {
		//	String backendName = volumeType.getMetadata().get(ParamConstant.STORAGE_BACKEND_NAME); // TODO // change  the param value
			String backendName = volumeType.getBackendName();
			for (StorageQuota storageQuota : storagesQuota) {
				if (backendName.equals(storageQuota.getVolumeBackendName())) {
					this.harsSpecInfo.addDiskInfo(backendName, storageQuota.getTotalCapacity());
				}
			}
		}
		return;
	}

	private Map<String, Integer> getusedFloatingIP() {
		// TODO
		// get the used floating ip from database
		Map<String, Integer> usedFloatingIP = new HashMap<String, Integer>();
		usedFloatingIP.put(ParamConstant.BGP_NETWORK, 0);
		usedFloatingIP.put(ParamConstant.TELECOM_NETWORK, 0);
		usedFloatingIP.put(ParamConstant.UNICOM_NETWORK, 0);
		usedFloatingIP.put(ParamConstant.MOBILE_NETWORK, 0);

		return usedFloatingIP;
	}
}
