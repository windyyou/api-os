package com.fnst.cloudapi.dao.common;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;

public interface ImageMapper  extends SuperMapper<Image, String> {
	public Integer countNum();

	public Integer countNumByInstanceStatus(String status);
	
	public Image selectByInstanceId(String id);
	
	public List<Image> selectListByInstanceId(String id);
	
	public List<Image> selectList();

	public List<Image> selectListForPage(int start, int end);
}
