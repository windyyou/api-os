package com.fnst.cloudapi.dao.common;
import java.util.List;

import com.fnst.cloudapi.pojo.common.Region;

public interface RegionMapper extends SuperMapper<Region,String>{

	public int countNum();
	public List<Region> selectList();
}
