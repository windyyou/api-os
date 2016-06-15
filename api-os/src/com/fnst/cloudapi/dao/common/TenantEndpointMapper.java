package com.fnst.cloudapi.dao.common;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fnst.cloudapi.pojo.common.CloudRole;
import com.fnst.cloudapi.pojo.common.TenantEndpoint;

public interface TenantEndpointMapper extends SuperMapper<TenantEndpoint,String>{
	
	public List<TenantEndpoint> selectListByTenantId(String ostenantid);
	
	public int deleteByTenantId(String ostenantid);

	public int deleteByTenantAndRegionId(String ostenantid,String belongRegion);
	
	public int deleteOne(TenantEndpoint tenantEndpoint);

}
