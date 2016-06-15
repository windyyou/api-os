package com.fnst.cloudapi.service.openstackapi;

import com.fnst.cloudapi.pojo.openstackapi.foros.User;

public interface UserService {
	
	public User createUser(User user) throws Exception;
	public boolean  deleteUser(User user) throws Exception;

}
