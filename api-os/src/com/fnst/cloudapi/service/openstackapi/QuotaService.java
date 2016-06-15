package com.fnst.cloudapi.service.openstackapi;

import java.util.List;
import java.util.Map;

import com.fnst.cloudapi.pojo.openstackapi.forgui.HardQuota;
import com.fnst.cloudapi.pojo.openstackapi.forgui.StorageQuota;

public interface QuotaService {
	public List<HardQuota> getHardQuotas(Map<String,String> paramMap,String tokenId);
	public Boolean setHardQuota(Map<String,String> paramMap,String tokenId);
	public List<StorageQuota> getStorageQuotas(Map<String,String> paramMap,String tokenId);
}
