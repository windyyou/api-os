package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.List;

import com.fnst.cloudapi.pojo.common.Util;

public class Instance {
	private String id;
	private String name;
	private String status;
	private String type;
	private Image image;
	private List<String> ips = new ArrayList<String>();
	private List<String> floatingIps = new ArrayList<String>();
	private String createdAt;
	private String fixedIp;
	private String floatingIp;
	private String networkId;
	
	public String getFixedIp() {
		if(null == ips || 0 == ips.size())
			return "";
		this.fixedIp = Util.listToString(ips, ',');
		return this.fixedIp;
	}

	public void setFixedIp(String fixedIp) {
		this.fixedIp = fixedIp;
	}

	public String getFloatingIp() {
		if(null == ips || 0 == ips.size())
			return "";
		this.floatingIp = Util.listToString(floatingIps, ',');
		return this.floatingIp;
	}

	public void setFloatingIp(String floatingIp) {
		this.floatingIp = floatingIp;
	}

	//Receive data from Portal
	private String sourceType;//image/snapshot
	private String sourceId; //image/snapshot's id
	private String sourceName;
	private String core;
	private String ram;
	private String networkType;
	private String subnet;
	private String credentialType;
	private String username;
	private String password;
	private String keypairId;
	private String keypairName;
	private String quantity;
	private String volumeType;
	private String volumeSize;
	//
    
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getStatus() {
		return this.status;
	}

	public void setImage(Image image) {
		this.image = image;
	}

	public Image getImage() {
		return this.image;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return this.type;
	}

	public List<String> getFloatingIps() {
		return floatingIps;
	}

	public void setFloatingIps(List<String> floatingIp) {
		this.floatingIps = floatingIp;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getCreatedAt() {
		return this.createdAt;
	}

	public List<String> getIps() {
		return ips;
	}

	public void setIps(List<String> ips) {
		this.ips = ips;
	}

	public void setSourceType(String sourceType){
		this.sourceType = sourceType;
	}
	
	public String getSourceType(){
		return this.sourceType;
	}
	
	public String getSourceId() {
		return sourceId;
	}

	public void setSourceId(String sourceId) {
		this.sourceId = sourceId;
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public void setCore(String core){
		this.core = core; 
	}
	
	public String getCore(){
		return this.core;
	}
	
	public void setRam(String ram){
		this.ram  = ram;
	}
	
	public String getRam(){
		return this.ram;
	}
	
	public void setNetworkType(String networkType){
		this.networkType = networkType;
	}
	
	public String getNetworkType(){
		return this.networkType;
	}
	
	public void setSubnet(String subnet){
		this.subnet = subnet;
	}
	
	public String getSubnet(){
		return this.subnet;
	}
	
	public void setCredentialType(String credentialType){
		this.credentialType = credentialType;
	}
	
	public String getCredentialType(){
		return this.credentialType;
	}
	
	public String getKeypairId() {
		return keypairId;
	}

	public void setKeypairId(String keypairId) {
		this.keypairId = keypairId;
	}

	public String getKeypairName() {
		return keypairName;
	}

	public void setKeypairName(String keypairName) {
		this.keypairName = keypairName;
	}

	public void setUsername(String username){
		this.username = username;
	}
	
	public String getUsername(){
		return this.username;
	}

	public void setPassword(String password){
		this.password = password;
	}
	
	public String getPassword(){
		return this.password;
	}
	
	public void setQuantity(String quantity){
		this.quantity = quantity;
	}
	
	public String getQuantity(){
		return this.quantity;
	}
	
	public void setVolumeType(String volumeType){
		this.volumeType = volumeType;
	}
	
	public String getVolumeType(){
		return this.volumeType;
	}
	
	public void setVolumeSize(String volumeSize){
		this.volumeSize = volumeSize;
	}
	
	public String getVolumeSize(){
		return this.volumeSize;
	}

	public String getNetworkId() {
		return networkId;
	}

	public void setNetworkId(String networkId) {
		this.networkId = networkId;
	}
	
}

