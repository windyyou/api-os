package com.fnst.cloudapi.dao.common;
import java.util.Date;

import com.fnst.cloudapi.pojo.common.DomainTenantUser;
import com.fnst.cloudapi.pojo.common.TokenOs;

public interface TokenOsMapper extends SuperMapper<TokenOs,String>{
	//查看Token是否存在
	public int countNum(String ostokenid);
	
	public TokenOs selectListByDomainTenantUserId(String tenantuserid);
	
	public TokenOs selectListByDomainTenantUser(DomainTenantUser domainTenantUser);
	
	public int deleteBytime(Date nowtime);

}
