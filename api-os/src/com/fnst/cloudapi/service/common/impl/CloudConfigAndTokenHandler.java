package com.fnst.cloudapi.service.common.impl;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

import com.fnst.cloudapi.pojo.common.CloudConfig;
import com.fnst.cloudapi.pojo.common.CloudUser;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.util.http.HttpClientForOsBase;

public abstract class CloudConfigAndTokenHandler {
	
	protected CloudConfig osConfig;
	protected TokenOs  osToken;
	private HttpClientForOsBase client;

	public CloudConfigAndTokenHandler(CloudUser user) {

		ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:com/fnst/conf/applicationContext.xml");
		osConfig=(CloudConfig) ac.getBean("cloudconfig");	
        this.UpdateConfigAndToken(user);
	}
	
	public CloudConfigAndTokenHandler() {

		ApplicationContext ac = new FileSystemXmlApplicationContext("classpath:com/fnst/conf/applicationContext.xml");
		osConfig=(CloudConfig) ac.getBean("cloudconfig");	
        this.getToken();
	}
	
	private void getToken(){
		this.client = new HttpClientForOsBase(osConfig);
		this.osToken=this.client.getToken();
		
	}
	
	public void UpdateConfigAndToken(CloudUser user){
		
		//todo 通过用户信息 从数据库里，找到此用户对应的openstack user,pwd,domainid,projectid
		//然后更当前osConfig.
		
		//然后更新token
		this.getToken();
	}		
	
}
