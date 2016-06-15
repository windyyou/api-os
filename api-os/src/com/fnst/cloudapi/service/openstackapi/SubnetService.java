package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;

public interface SubnetService {

	public List<Subnet> getSubnetList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Subnet getSubnet(String subnetId, String guiTokenId,HttpServletResponse response) throws BusinessException;	
	public Subnet createSubnet(String createBody,String tokenId,HttpServletResponse response) throws BusinessException;
}
