package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.FloatingIP;

public interface FloatingIPService {
	
	public List<FloatingIP> getFloatingIPList(Map<String, String> paramMap, String tokenId,HttpServletResponse response) throws BusinessException;
	public FloatingIP getFloatingIP(String floatingIpId, String tokenId,HttpServletResponse response) throws BusinessException;
	public FloatingIP createFloatingIp(String createBody, String tokenId,HttpServletResponse response) throws BusinessException;
	
	public List<FloatingIP> getFloatingIPExtList(Map<String, String> paraMap, String tokenId) throws BusinessException;

}
