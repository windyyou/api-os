package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Keypair;

public interface KeypairService {
	public List<Keypair> getKeypairList(Map<String,String> paraMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Keypair createKeypair(Map<String,String> paraMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Keypair getKeypair(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
}
