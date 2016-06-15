package com.fnst.cloudapi.service.common.impl;

import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.pojo.common.TokenGui;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.service.common.AuthService;
import com.fnst.cloudapi.util.http.HttpClientForOsBase;

public class AuthServiceImpl extends CloudConfigAndTokenHandler implements AuthService {

	@Override
	public boolean authLogin(CloudUser user) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TokenGui authCeate(CloudUser user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean authCheck(TokenGui tokengui) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public TokenOs GetTokenOS(CloudUser user) {
		
		//TODO 1 get the info of user for os. 
			
		HttpClientForOsBase client =new HttpClientForOsBase(super.osConfig);
		return client.getToken();
	}
	
	public TokenOs  GetTokenOS(String guiTokenId){
		
		return null;
	}

}
