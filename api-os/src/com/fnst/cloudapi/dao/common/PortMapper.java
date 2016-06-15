package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Port;

public interface PortMapper extends SuperMapper<Port, String> {
	public Integer countNum();
	
	public Port selectByNetworkId(String id);
	
	public List<Port> selectListByNetworkId(String id);
	
	public List<Port> selectAllList();

	public List<Port> selectListForPage(int start, int end);

}
