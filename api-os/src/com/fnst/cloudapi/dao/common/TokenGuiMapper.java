package com.fnst.cloudapi.dao.common;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.fnst.cloudapi.pojo.common.CloudRole;
import com.fnst.cloudapi.pojo.common.TokenGui;

public interface TokenGuiMapper extends SuperMapper<TokenGui,String>{
	
	public int countNum(String guitokenid);
	
	public  List<TokenGui> selectListByUser(String userid);
	
	//根据设定的有效时间，清除所有超出有效期的token
	public int deleteBytime(long nowtime);

}
