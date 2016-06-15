package com.fnst.cloudapi.pojo.common;

public  class CloudUser {
	
	private String userid;
	//登陆账号
	private String code;
	//用户姓名
	private String name;
	private String password;
	private String mail;
	private String phone;
	private String company;
	
	/***********os link**************/
	private String ostenantid;
	private String osdomainid;
	
//	public CloudUser(){
//		
//	}
//	public CloudUser(String code, String name, int sex, Date birthday, String password, String mail,
//			String phone) {
//		super();
//		this.code = code;
//		this.name = name;
//		this.sex = sex;
//		this.birthday = birthday;
//		this.password = password;
//		this.mail = mail;
//		this.phone = phone;
//	}
//	
//	public CloudUser(String userid, String code, String name, int sex, Date birthday, String password, String mail,
//			String phone, String ostenantid, String osdomainid) {
//		super();
//		this.userid = userid;
//		this.code = code;
//		this.name = name;
//		this.sex = sex;
//		this.birthday = birthday;
//		this.password = password;
//		this.mail = mail;
//		this.phone = phone;
//		this.ostenantid = ostenantid;
//		this.osdomainid = osdomainid;
//	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMail() {
		return mail;
	}

	public void setMail(String mail) {
		this.mail = mail;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getOstenantid() {
		return ostenantid;
	}

	public void setOstenantid(String ostenantid) {
		this.ostenantid = ostenantid;
	}

	public String getOsdomainid() {
		return osdomainid;
	}

	public void setOsdomainid(String osdomainid) {
		this.osdomainid = osdomainid;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}
	
	
}
