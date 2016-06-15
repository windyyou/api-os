package com.fnst.cloudapi.pojo.openstackapi.forgui;

public class StorageQuota {
     private float totalCapacity;
     private float freeCapacity;
     private String volumeBackendName;
     private String storageProtocol;
     private String name;
     private String poolName;
     
     public void setName(String name){
    	 this.name = name;
     }
     
     public String getName(){
    	 return this.name;
     }
     
     public void setPoolName(String poolName){
    	 this.poolName = poolName;
     }
     
     public String getPoolName(){
    	 return this.poolName;
     }
     
     public void setTotalCapacity(float totalCapacity){
    	 this.totalCapacity = totalCapacity;
     }
     
     public float getTotalCapacity(){
    	 return this.totalCapacity;
     }
     
     public void setFreeCapacity(float freeCapacity){
    	 this.freeCapacity = freeCapacity;
     }
     
     public float getFreeCapacity(){
    	 return this.freeCapacity;
     }
     
     public void setVolumeBackendName(String volumeBackendName){
    	 this.volumeBackendName = volumeBackendName;
     }
     
     public String getVolumeBackendName(){
    	 return this.volumeBackendName;
     }
     
     public void setStorageProtocol(String storageProtocol){
    	 this.storageProtocol = storageProtocol;
     }
     
     public String getStorageProtocol(){
    	 return this.storageProtocol;
     }
     
   
}
