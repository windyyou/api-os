package com.fnst.cloudapi.service.openstackapi;

import java.util.List;

import com.fnst.cloudapi.pojo.openstackapi.foros.Role;

public interface RoleService {
	
	public Role createRole(Role role) throws Exception;
	
	public List<Role> getRoleList() throws Exception;
	
	public Role getRoleByName(String name) throws Exception;
	
	public boolean  deleteRole(Role role) throws Exception;
	
	public boolean  grantRoleToUserOnProject(String role_id,String user_id,String project_id) throws Exception;

}
