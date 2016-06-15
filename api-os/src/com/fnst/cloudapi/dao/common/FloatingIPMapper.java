package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.FloatingIP;

/** 
* @author  wangw.fnst@cn.fujitsu.com 
* @create  2016年5月31日 上午11:52:51 
* 
*/
public interface FloatingIPMapper extends SuperMapper<FloatingIP,String> {
	
	public Integer countNum();

	public Integer countNumByInstanceStatus(String status);
	
	public FloatingIP selectByInstanceId(String id);
	
	public FloatingIP selectByRouterId(String id);
	
	public List<FloatingIP> selectListByInstanceId(String id);
	
	public List<FloatingIP> selectListByRouterId(String id);
	
	public List<FloatingIP> selectList();

	public List<FloatingIP> selectListForPage(int start, int end);

}
