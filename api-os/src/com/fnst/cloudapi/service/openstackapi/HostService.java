package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Host;

public interface HostService {
	public List<Host> getHostList(Map<String,String> paramMap,String tokenId);
	public Host getHostDetails( String hostName,String tokenId);
}
