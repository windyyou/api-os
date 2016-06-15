package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Flavor;

public interface FlavorService {
	public List<Flavor> getFlavorList(Map<String,String> paramMap,String tokenId);
	public Flavor createFlavor(Map<String,String> paramMap,String tokenId);
	public Flavor getFlavor(Map<String,String> paramMap,String tokenId);
}
