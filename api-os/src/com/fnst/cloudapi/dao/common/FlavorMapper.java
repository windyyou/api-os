package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Flavor;


/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年5月31日 上午11:52:51 
* 
*/
public interface FlavorMapper extends SuperMapper<Flavor,String> {
	
	public List<Flavor> selectAll();

	public List<Flavor> selectAllForPage(int start, int end);

}
