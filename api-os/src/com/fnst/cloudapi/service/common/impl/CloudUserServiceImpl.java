package com.fnst.cloudapi.service.common.impl;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Service;

import com.fnst.cloudapi.dao.common.CloudUserMapper;
import com.fnst.cloudapi.dao.common.SuperMapper;
import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.pojo.openstackapi.foros.Project;
import com.fnst.cloudapi.pojo.openstackapi.foros.Role;
import com.fnst.cloudapi.pojo.openstackapi.foros.User;
import com.fnst.cloudapi.service.common.CloudRegionService;
import com.fnst.cloudapi.service.common.CloudUserService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.RoleService;
import com.fnst.cloudapi.service.openstackapi.TenantService;
import com.fnst.cloudapi.service.openstackapi.UserService;
import com.fnst.cloudapi.util.ExceptionMessage;
import com.fnst.cloudapi.util.OpenStackBaseConstant;
import com.fnst.cloudapi.util.UUIDHelper;

@Service
public class CloudUserServiceImpl extends SuperDaoServiceImpl<CloudUser, String> implements CloudUserService {
    @Resource
    CloudUserMapper cloudUserMapper;
    @Resource
    CloudRegionService cloudRegionService;
//    @Resource
//    TenantService tenantService;
//    @Resource
//    UserService   userService;
//    @Resource
//    RoleService   roleService;
    
    private Logger log = LogManager.getLogger(CloudUserServiceImpl.class);
    
	@Override
	public SuperMapper<CloudUser, String> getMapper(){
		// TODO Auto-generated method stub
		return this.cloudUserMapper;
	}


	@Override
	public List<CloudUser> selectList()throws Exception {
		// TODO Auto-generated method stub
		List<CloudUser> userlist=cloudUserMapper.selectList();
		return userlist;
	}

	@Override
	public List<CloudUser> selectListForPage(int start, int end)throws Exception {
		// TODO Auto-generated method stub
		List<CloudUser> userlist=cloudUserMapper.selectListForPage(start, end);
		return userlist;
	}

	@Override
	public int countNum() throws Exception{
		// TODO Auto-generated method stub
		int num=this.cloudUserMapper.countNum();
		return num;
	}

	@Override
	public CloudUser insertUserAndTenant(CloudUser user) throws Exception {

		// step1:重复性check： 如果此账户已存在，则抛出异常
		int num = this.cloudUserMapper.countNumByUserCode(user.getCode());
		if (num != 0)
			throw new Exception(ExceptionMessage.USER_ALREADY_EXIST);

		// step2:访问openstack 根据用户"tenant_"+code为tenant名创建tenant
		Project project = new Project();
		project.setName("tenant_" + user.getCode());
		project.setEnabled(true);
		project.setDescription("The tenant for user:" + user.getCode());
	    TenantService tenantService=OsApiServiceFactory.getTenantService();
		try {
			project = tenantService.createProject(project);
		} catch (Exception e) {

			log.error("create project failed:", e);
			throw e;

		}
		// step3:创建openstack user
		User osuser = new User();
		osuser.setDefault_project_id(project.getId());
		osuser.setDescription("project:" + project.getName() + " user:" + user.getName());
		osuser.setEmail(user.getMail());
		osuser.setEnabled(true);
		osuser.setName("OS_"+user.getName());
		osuser.setPassword("OS_"+user.getPassword());
		
	    UserService   userService=OsApiServiceFactory.getUserService();
		try {
			osuser = userService.createUser(osuser);
		} catch (Exception e) {

			log.error("create user failed:", e);
			throw e;

		}

		// step4:获取名字叫user的role
	    RoleService   roleService=OsApiServiceFactory.getRoleService();		
		Role role = null;
		try {
			role=roleService.getRoleByName(OpenStackBaseConstant.OS_CLOUDUSER_ROLE);
		} catch (Exception e) {

			log.error("get user role failed:", e);
			throw e;

		}

		// step5:user,role,project权限绑定
		try {
			if(!roleService.grantRoleToUserOnProject(role.getId(), osuser.getId(), project.getId())){
			  throw new Exception("grant role to user on project:noexception but false");
			}
		} catch (Exception e) {

			log.error("grant role to user on project failed:", e);
			throw e;

		}

		// step6:中间层数据库保存
		//手动生成UUID
		user.setUserid(UUIDHelper.buildUUIDStr());
		// 设置从openstack的返回体中取出tenant 信息
		user.setOstenantid(project.getId());
		user.setOsdomainid(project.getDomain_id());
		try {
			super.insert(user);
		} catch (Exception e) {
			log.error("inter user to apidabase failed :", e);
			throw e;
		}
		
		// step7:@todo 使用tenantid(projectid)调用 zabbix层API 创建group
		
		return user;
	}
	
	
	public static void main(String arg[]){
		
		CloudUser user =new CloudUser();
        user.setUserid(UUIDHelper.buildUUIDStr());
		user.setCode("cbl001");
		user.setPassword("cbl001");
		user.setName("崔保亮");
		user.setMail("cuibl@cn.fujitsu.com");
        user.setPhone("13675106341");
        user.setCompany("cbl cloud ");
        
		try {
			CloudUserServiceImpl impl=new CloudUserServiceImpl();
			CloudUser userget=impl.insertUserAndTenant(user);
			assertNotNull(userget);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        	
	}

}
