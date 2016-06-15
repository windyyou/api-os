package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Keypair;

public interface KeypairMapper extends SuperMapper<Keypair, String>{
	public Integer countNum();

	public Integer countNumByInstanceStatus(String status);
	
	public Keypair selectByInstanceId(String id);
	
	public List<Keypair> selectListByInstanceId(String id);
	
	public List<Keypair> selectList();

	public List<Keypair> selectListForPage(int start, int end);
}
