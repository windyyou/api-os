package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Router;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年5月30日 下午4:29:49 
* 
*/
public interface RouterMapper extends SuperMapper<Router,String>{
	public List<Router> selectAll();
	public List<Router> selectList();
	public List<Router> selectAllForPage(int start, int end);
	

}
