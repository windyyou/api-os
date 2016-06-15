package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;

public interface NetworkMapper extends SuperMapper<Network,String>{
	
	public List<Network> selectAllList();
	public List<Network> selectListByTenantID(String tenant_id);
	public  int insertInstancesNetworks(Network network);
	public  int insertTenantsNetworks(Network network);
	public  int deleteInstancesNetworks(String network_id);
	public  int deleteTenantsNetworks(String network_id);

}
