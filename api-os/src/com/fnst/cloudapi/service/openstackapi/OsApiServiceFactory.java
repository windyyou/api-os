package com.fnst.cloudapi.service.openstackapi;


import com.fnst.cloudapi.service.openstackapi.impl.BackupServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.FlavorServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.FloatingIPServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.HostServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.ImageServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.InstanceServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.KeypairServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.MonitorServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.NetworkServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.NotificationListServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.NotificationServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.PoolServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.QuotaServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.RoleServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.SubnetServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.TenantServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.UserServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.VolumeServiceImpl;

public class OsApiServiceFactory {
//    @Resource
//    private static TenantService tenantService;
//    
//    @Resource  
//    private static UserService userService;
//    
//    @Resource  
//    private static RoleService roleService; 
    
	public static InstanceService getInstanceService(){
		
		return new InstanceServiceImpl();
	}
	
    public static NetworkService getNetworkService(){
		
		return new NetworkServiceImpl();
	}
	
	public static VolumeService getVolumeService() {
		
		return new VolumeServiceImpl();
	}

	public static ImageService getImageService(){
		
		return new ImageServiceImpl();
	}
	
	public static KeypairService getkeypairService(){
		
		return new KeypairServiceImpl();
	}
	
	public static BackupService getBackupService() {
		
		return new BackupServiceImpl();
	}
	
	public static HostService getHostService(){
		return new HostServiceImpl();
	}
	
	public static QuotaService getQuotaService(){
		return new QuotaServiceImpl();
	}
	
	public static TenantService getTenantService(){
		
		return new TenantServiceImpl();			
//		return tenantService;
	}
	
	public static FloatingIPService getFloatingIPService(){
		return new FloatingIPServiceImpl();
	}
	
	public static SubnetService getSubnetService(){
		return new SubnetServiceImpl();
	}
	
	public static FlavorService getFlavorService(){
		return new FlavorServiceImpl();
	}
	
	public static PoolService getPoolService(){
		return new PoolServiceImpl();
	}

	public static NotificationService getNotificationService(){
		return new NotificationServiceImpl();
	}
	
	public static NotificationListService getNotificationListService() {
		return new NotificationListServiceImpl();
	}
	
	public static UserService getUserService(){
	
		return new UserServiceImpl();	
		
//		return userService;
		
	}
	
	public static RoleService getRoleService(){
		
		return new RoleServiceImpl();
		
//		return roleService;		
	}
	
	public static MonitorService getMonitorService(){
		return new MonitorServiceImpl();
	}
	
	
}
