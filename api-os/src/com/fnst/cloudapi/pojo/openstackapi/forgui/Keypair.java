package com.fnst.cloudapi.pojo.openstackapi.forgui;

public class Keypair {

	private String fingerprint;
	private String name;
	private String public_key;
	private String user_id;
	private String private_key;
	private String created_at;
	private String instanceId;
	private String privateKeyPath;
	private String id;
	
	public Keypair(){
		this.fingerprint = "";
		this.name = "";
		this.public_key = "";
//		this.user_id = "";
//		this.private_key = "";
	}
	
	public String getPrivateKeyPath() {
		return privateKeyPath;
	}

	public void setPrivateKeyPath(String privateKeyPath) {
		this.privateKeyPath = privateKeyPath;
	}

	public Keypair(String name){
		this.name = name;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	public String getCreated_at() {
		return created_at;
	}

	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}

	public Keypair(String name,String public_key){
		this.name = name;
		this.public_key = public_key;
	}
	
	public Keypair(String name,String public_key,String fingerprint){
		this.name = name;
		this.public_key = public_key;
		this.fingerprint = fingerprint;
	}
	
	public void setFingerprint(String fingerprint){
		this.fingerprint = fingerprint;
	}
	
	public String getFingerprint(){
		return this.fingerprint;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}

	public String getPublic_key() {
		return public_key;
	}

	public void setPublic_key(String public_key) {
		this.public_key = public_key;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getPrivate_key() {
		return private_key;
	}

	public void setPrivate_key(String private_key) {
		this.private_key = private_key;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
}
