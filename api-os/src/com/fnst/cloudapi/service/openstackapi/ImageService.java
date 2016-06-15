package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;

public interface ImageService {
	public List<Image> getImageList(Map<String,String> paraMap,String tokenId,HttpServletResponse response) throws BusinessException;
	public Image getImage(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException;
}
