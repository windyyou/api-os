package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;

public interface SubnetMapper extends SuperMapper<Subnet,String>{
	
	public List<Subnet> selectAllList();
	public List<Subnet> selectListByTenantId(String tenant_id);
	public List<Subnet> selectListByNetworkId(String network_id);
	
	public  int insertTenantsSubnets(Subnet subnet);
	public  int deleteTenantsSubnets(String subnet_id);


}
