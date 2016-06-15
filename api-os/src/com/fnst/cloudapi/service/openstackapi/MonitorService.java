package com.fnst.cloudapi.service.openstackapi;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;


public interface MonitorService {
	public void createMonitorObj(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException;
	public void deleteMonitorObj(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException;
	public void createMonitorNotification(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException;
	public void deleteMonitorNotification(String createBody,String guiTokenId,HttpServletResponse response) throws BusinessException;
}
