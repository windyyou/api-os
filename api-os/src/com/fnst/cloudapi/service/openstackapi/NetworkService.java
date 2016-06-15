package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Port;
import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroup;
import com.fnst.cloudapi.pojo.openstackapi.forgui.SecurityGroupRule;

public interface NetworkService {

	public List<Network> getNetworkList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Network getNetwork(String networkId, String guiTokenId,HttpServletResponse response) throws BusinessException;	
	public Network createNetwork(String createBody,String tokenId,HttpServletResponse response) throws BusinessException;
	public List<Port> getPortList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Port getPort(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Port createPort(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException;

	public List<SecurityGroup> getSecurityGroupList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public SecurityGroup getSecurityGroup(String securityGroupId,String tokenId,HttpServletResponse response) throws BusinessException;
	public SecurityGroup createSecurityGroup(String createBody,String tokenId,HttpServletResponse response) throws BusinessException;
	
	public List<SecurityGroupRule> getSecurityGroupRuleList(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public SecurityGroupRule createSecurityGroupRule(String createBody,String tokenId,HttpServletResponse response) throws BusinessException;
}
