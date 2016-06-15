package com.fnst.cloudapi.service.hello;

import java.io.IOException;
import java.util.List;

import org.openstack4j.api.Builders;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.identity.ServiceManagerService;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.Service;
import org.openstack4j.model.identity.ServiceEndpoint;
import org.openstack4j.model.identity.Tenant;
import org.openstack4j.model.identity.User;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.image.Image;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.identity.domain.v3.KeystoneProject;


public class TestOS4J {
    
//	private final static String ks_adminurl="http://193.160.31.45:35357/v3";
//	private final static String ks_puburlv2="http://193.160.31.45:5000/v2.0";
//	private final static String ks_puburlv3="http://193.160.31.45:5000/v3";
//	private final static String ks_admin="admin";
//	private final static String ks_adminpwd="ADMIN_PASS";
//	private final static String ks_admindomain="default";
	
	private final static String ks_adminurl="http://193.168.141.33:35357/v3/";
	private final static String ks_puburlv2="http://193.168.141.33:5000/v2.0/";
	private final static String ks_puburlv3="http://193.168.141.33:5000/v3/";
	private final static String ks_admin="admin";
	private final static String ks_adminpwd="elb1234";
	private final static String ks_admindomain="default";
	
	
	public static String test(){
		
//		OSClient os = OSFactory.builder()
//                .endpoint(ks_adminurl)
//                .credentials(ks_admin,ks_adminpwd)
//                .tenantName("admin")
//                .authenticate();
//		// Find all Users
//		List<? extends User> users = os.identity().users().list();
//
//		// List all Tenants
//		List<? extends Tenant> tenants = os.identity().tenants().list();
//
//		// Find all Compute Flavors
//		List<? extends Flavor> flavors = os.compute().flavors().list();
//
//		// Find all running Servers
//		List<? extends Server> servers = os.compute().servers().list();
//
//		// Suspend a Server
//		//os.compute().servers().action("serverId", Action.SUSPEND);
//
//		// List all Networks
//		List<? extends Network> networks = os.networking().network().list();
//
//		// List all Subnets
//		List<? extends Subnet> subnets = os.networking().subnet().list();
//
//		// List all Routers
//		List<? extends Router> routers = os.networking().router().list();
//
//		// List all Images (Glance)
//		List<? extends Image> images = os.images().list();

		
		return "";
	}
	
	public static List<? extends User> getUsers(){
	
//   Identifier domainIdentifier = Identifier.byName("default");
//	 OSClient os = OSFactory.builderV3().endpoint(ks_adminurl).credentials(ks_admin,ks_adminpwd,domainIdentifier).authenticate();
		
	 OSClient os = OSFactory.builder().endpoint(ks_puburlv2).credentials(ks_admin,ks_adminpwd).tenantName("admin").authenticate();	
		
	 List<? extends User> users=null;
      if (os==null){  
    	System.out.println("openstack4j-test: osclient is null");
      }else{
       users = os.identity().users().list();	
       System.out.println("openstack4j-test: "+users.toString());
      }
	  return users;
	}
	
	public static List<? extends Image> getImages(){
		
	//   Identifier domainIdentifier = Identifier.byName("default");
    //	 OSClient os = OSFactory.builderV3().endpoint(ks_adminurl).credentials(ks_admin,ks_adminpwd,domainIdentifier).authenticate();
		OSClient os =null;
		try {
		Identifier domainIdentifier = Identifier.byName(ks_admindomain);
		os = OSFactory.builderV3().endpoint(ks_puburlv3).credentials(ks_admin,ks_adminpwd,domainIdentifier).authenticate();			
		//OSClient os = OSFactory.builder().endpoint(ks_puburlv2).credentials(ks_admin,ks_adminpwd).tenantName("admin").authenticate();	
		}catch (Exception e) {

			System.out.println("openstack4j-test: "+e.toString());
			e.printStackTrace();
		}
		
		 List<? extends Image> images=null;
	      if (os==null){  
	    	System.out.println("openstack4j-test: osclient is null");
	      }else{
	    	images = os.images().list();	
	    	System.out.println("openstack4j-test: "+images.toString());
	      }
		  return images;
	}
	
	public static List<? extends Tenant> getTenants(){
		
	//   Identifier domainIdentifier = Identifier.byName("default");
    //   OSClient os = OSFactory.builderV3().endpoint(ks_adminurl).credentials(ks_admin,ks_adminpwd,domainIdentifier).authenticate();
			
		 OSClient os = OSFactory.builder().endpoint(ks_puburlv2).credentials(ks_admin,ks_adminpwd).tenantName("admin").authenticate();	
			
		 List<? extends Tenant> list=null;
	      if (os==null){  
	    	System.out.println("openstack4j-test: osclient is null");
	      }else{
	    	  list = os.identity().tenants().list();	
	    	System.out.println("openstack4j-test: "+list.toString());
	      }
		  return list;
	}
	
	public static Tenant createTenantv2(String name,String disc){
		
		Tenant tt=null;
//		Identifier domainIdentifier = Identifier.byName("default");
//		OSClient os = OSFactory.builderV3().endpoint(ks_puburlv3).credentials(ks_admin,ks_adminpwd,domainIdentifier).authenticate();
		
		OSClient os = OSFactory.builder().endpoint(ks_puburlv2).credentials(ks_admin,ks_adminpwd).tenantName("admin").authenticate();
	    if (os==null){  
	    	System.out.println("openstack4j-test: osclient is null");
	      }else{
	    	  tt =new Builders().tenant().name(name).description(disc).build();
	    	  System.out.println("openstack4j-test: "+tt.toString());  
	    	  tt= os.identity().tenants().create(tt);
	    	  System.out.println("openstack4j-test: "+tt);  
	    	  if(tt==null || tt.getId() ==null||"".equals(tt.getId())){
	    		System.out.println("openstack4j-test:created failed ");  
	    	  }else{
	    		System.out.println("openstack4j-test:created ok id "+tt.getId());
	    	  }
	     }
		
		return tt;
	}
	
	public static Project createTenantv3(String name,String disc){
		
		Project tt=null;
		Identifier domainIdentifier = Identifier.byName("default");
	    OSClient os = OSFactory.builderV3().endpoint(ks_adminurl).credentials(ks_admin,ks_adminpwd,domainIdentifier).authenticate();
	    if (os==null){  
	    	System.out.println("openstack4j-test: osclient is null");
	      }else{
	    	  tt =KeystoneProject.builder().name(name).domainId("default").description(disc).build();
	    	  System.out.println("openstack4j-test: "+tt.toString());  
	    	 
	    	  System.out.println("openstack4j-test: "+tt);  
	    	  if(tt==null || tt.getId() ==null||"".equals(tt.getId())){
	    		System.out.println("openstack4j-test:created failed ");  
	    	  }else{
	    		System.out.println("openstack4j-test:created ok id "+tt.getId());
	    	  }
	     }
		
		return tt;
	}
	
	
	public static void justTestv3(){
		
		Identifier domainIdentifier = Identifier.byName("default");
	    OSClient os = OSFactory.builderV3().endpoint(ks_adminurl).credentials(ks_admin,ks_adminpwd,domainIdentifier).authenticate();
	    System.out.println("openstack4j-test:v3-token:"+os.getToken().toString());

	}
	
	public static void justTestv2(){
		
	    OSClient os = OSFactory.builder().endpoint(ks_puburlv2).credentials(ks_admin,ks_adminpwd).tenantName("admin").authenticate();
	    System.out.println("openstack4j-test:v2-token:"+os.getToken().toString());
	    // Lets cut down our method chaining and pre-assign the ServiceManagerService API
	    ServiceManagerService sm = os.identity().services();

	    // List Services
	    List<? extends Service> services = sm.list();
	    System.out.println("openstack4j-test:v2-service-size:"+services.size());
	    for (Service one:services){
	    	System.out.println("openstack4j-test:v2-service:"+one.toString());
	    }

	    // List Endpoints
	    List<? extends ServiceEndpoint> ep = sm.listEndpoints();
	    System.out.println("openstack4j-test:v2-ServiceEndpoint-size:"+ep.size());
	    for (ServiceEndpoint one:ep){
	    	System.out.println("openstack4j-test:v2-ServiceEndpoint:"+one.toString());
	    }

	}
	
	public static void main(String arg[]){
		System.out.println("openstack4j-test:v2-token");
		justTestv2();
//		System.out.println("openstack4j-test:v3-token");
//		justTestv3();
		
		System.out.println(TestOS4J.createTenantv2("cbltenant","justest").toString());
		
		System.out.println(TestOS4J.getTenants().toString());
		
	}

	
	
	
}
