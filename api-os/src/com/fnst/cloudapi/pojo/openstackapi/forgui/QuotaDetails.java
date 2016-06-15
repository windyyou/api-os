package com.fnst.cloudapi.pojo.openstackapi.forgui;

public class QuotaDetails {
   private int reserved;
   private int limit;
   private int in_use;
   
   public QuotaDetails(){
	   this.reserved = 0;
	   this.limit = 0;
	   this.in_use = 0;
   }
   
   public QuotaDetails(int reserved,int limit,int in_use){
	   this.reserved = reserved;
	   this.limit = limit;
	   this.in_use = in_use;
   }
   
   public void setReserved(int reserved){
	   this.reserved = reserved;
   }
   
   public int getReserved(){
	   return this.reserved;
   }
   
   public void setLimit(int limit){
	   this.limit = limit;
   }
   
   public int getLimit(){
	   return this.limit;
   }
   
   public void setInUse(int in_use){
	   this.in_use = in_use;
   }
   
   public int getInUse(){
	   return this.in_use;
   }
}
