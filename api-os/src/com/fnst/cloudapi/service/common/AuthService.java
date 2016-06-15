package com.fnst.cloudapi.service.common;

import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.pojo.common.TokenGui;
import com.fnst.cloudapi.pojo.common.TokenOs;

public interface AuthService {
	
	/**
	 * 用户登录验证
	 * @param user
	 * @return
	 */
	public boolean authLogin(CloudUser user);
	
	/**
	 * 为新用户生成用于web访问的Token
	 * @param user
	 * @return
	 */
	public TokenGui authCeate(CloudUser user);
	
	/**
	 * 检查所给Token是否有效
	 * @param tokengui
	 * @return
	 */
	public boolean  authCheck(TokenGui tokengui);
	
	/**
	 * 取得用于Openstack 访问的Token
	 * @param user
	 * @return
	 */
	public TokenOs  GetTokenOS(CloudUser user);
	
	
	/**
	 * 取得用于Openstack 访问的Token
	 * @param user
	 * @return
	 */
	public TokenOs  GetTokenOS(String guiTokenId);
	
}
