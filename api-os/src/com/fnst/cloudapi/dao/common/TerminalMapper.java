package com.fnst.cloudapi.dao.common;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Terminal;

/** 
* @author  hous.jy@cn.fujitsu.com (2016-6-2 13:28:31)
* 
*/
public interface TerminalMapper extends SuperMapper<Terminal,String> {
	
	public int deleteByUpdateDelFlag(String id) throws BusinessException;
	
}
