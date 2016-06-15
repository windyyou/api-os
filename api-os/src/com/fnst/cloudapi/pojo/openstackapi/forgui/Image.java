package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

//只序列化非NULL的值
@JsonInclude(value = Include.NON_NULL)
public class Image {
	private String id;
	private String name;
	private String status;
	private List<String> tags;
	private String createdAt;
	private String updatedAt;
	private String visibility;
	private Boolean isProtected;
	private String file;
	private String owner;
	private String diskFormat;
	private String minDisk;
	private String size;
	private String minRam;
	private String instanceId;
	private String systemName;

	public Image() {
		this.id = new String();
		this.name = new String();
		this.status = new String();
		this.instanceId = new String();
		this.createdAt = new String();
	}

	public Image(String id, String name) {
		this.id = id;
		this.name = name;
		this.status = new String();
		this.instanceId = new String();
		this.createdAt = new String();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSystemName() {
		return systemName;
	}

	public void setSystemName(String systemName) {
		this.systemName = systemName;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public String getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(String updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		this.visibility = visibility;
	}

	public Boolean getIsProtected() {
		return isProtected;
	}

	public void setIsProtected(Boolean isProtected) {
		this.isProtected = isProtected;
	}

	public String getFile() {
		return file;
	}

	public void setFile(String file) {
		this.file = file;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getDiskFormat() {
		return diskFormat;
	}

	public void setDiskFormat(String diskFormat) {
		this.diskFormat = diskFormat;
	}

	public String getMinDisk() {
		return minDisk;
	}

	public void setMinDisk(String minDisk) {
		this.minDisk = minDisk;
	}

	public String getSize() {
		return size;
	}

	public void setSize(String size) {
		this.size = size;
	}

	public String getMinRam() {
		return minRam;
	}

	public void setMinRam(String minRam) {
		this.minRam = minRam;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

}