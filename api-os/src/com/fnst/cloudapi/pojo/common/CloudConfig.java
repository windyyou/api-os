package com.fnst.cloudapi.pojo.common;

public  class CloudConfig {
	
	private String os_authurl;
	private String os_authuser;
	private String os_authpwd;
	private String os_authdomainid;
	private String os_authtenantid;
	private String os_defaultregion;
	
	
	
	public CloudConfig() {
		super();
	}


	public CloudConfig(String os_authurl, String os_authuser, String os_authpwd) {
		super();
		this.os_authurl = os_authurl;
		this.os_authuser = os_authuser;
		this.os_authpwd = os_authpwd;
	}
	
	
	public CloudConfig(String os_authurl, String os_authuser, String os_authpwd, String os_authdomainid,
			String os_authetenantid) {
		super();
		this.os_authurl = os_authurl;
		this.os_authuser = os_authuser;
		this.os_authpwd = os_authpwd;
		this.os_authdomainid = os_authdomainid;
		this.os_authtenantid = os_authetenantid;
	}


	public String getOs_authurl() {
		return os_authurl;
	}
	public void setOs_authurl(String os_authurl) {
		this.os_authurl = os_authurl;
	}
	public String getOs_authuser() {
		return os_authuser;
	}
	public void setOs_authuser(String os_authuser) {
		this.os_authuser = os_authuser;
	}
	public String getOs_authpwd() {
		return os_authpwd;
	}
	public void setOs_authpwd(String os_authpwd) {
		this.os_authpwd = os_authpwd;
	}

	public String getOs_authdomainid() {
		return os_authdomainid;
	}

	public void setOs_authdomainid(String os_authdomainid) {
		this.os_authdomainid = os_authdomainid;
	}

	public String getOs_authtenantid() {
		return os_authtenantid;
	}

	public void setOs_authtenantid(String os_authetenantid) {
		this.os_authtenantid = os_authetenantid;
	}


	public String getOs_defaultregion() {
		return os_defaultregion;
	}


	public void setOs_defaultregion(String os_defaultregion) {
		this.os_defaultregion = os_defaultregion;
	}
	

}
