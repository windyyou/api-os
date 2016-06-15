package com.fnst.cloudapi.pojo.common;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.fnst.cloudapi.util.ParamConstant;

public class Util {
	
	public static String listToString(List list, char separator) {    
		if(null == list || 0 == list.size())
			return "";
		StringBuilder sb = new StringBuilder();    
		int length = list.size();
		for (int i = 0; i < length; i++) {        
			sb.append(list.get(i)); 
			if (i < length - 1)   
				sb.append(separator); 
		}   
		return sb.toString();
	}
	
	public static boolean isSystemVolume(String name){
		if(null == name)
			return false;
		int index = name.indexOf("/dev/");
		if(-1 == index)
			return false;
		String deviceName = name.substring(index+"/dev/".length());
		if(deviceName.equals("vda") || deviceName.equals("hda") || deviceName.equals("sda"))
			return true;
		return false;
	}
	
	public static String getImageIdFromLocation(String location){
		if(null == location)
			return null;
		int imagePos = location.indexOf(ParamConstant.IMAGES);
		if(-1 == imagePos)
			return null;
		int length = (ParamConstant.IMAGES+"/").length();
		return location.substring(imagePos+length);
	}
	
	public static String getCurrentDate(){
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return df.format(new Date());
	}
	
	public static String getIdWithAppendId(String appendId,String orgId){
		if(Util.isNullOrEmptyValue(orgId))
			return appendId;
		StringBuilder sb = new StringBuilder();
		sb.append(orgId);
		sb.append(',');
		sb.append(appendId);
		return sb.toString();
	}


	public static String getCreateBody(String bodyName,Map<String,String> paramMap){
		if(null == paramMap || 0 == paramMap.size())
			return null;
		StringBuilder sb = new StringBuilder();
		sb.append(bodyName);
		sb.append(":{");
		int i = 0;
		for (Entry<String, String> entry : paramMap.entrySet()) {
			sb.append(entry.getKey());
			sb.append(":");
			sb.append( entry.getValue());
			if (i < paramMap.size() - 1)   
				sb.append(','); 
			++i;
		}
		sb.append("}");
		return sb.toString();
	}
	
	public static boolean isNullOrEmptyValue(String value){
		if(null == value || value.isEmpty())
			return true;
		return false;
	}
	
	public static boolean isNullOrEmptyList(List list){
		if(null == list || 0 == list.size())
			return true;
		return false;
	}
}
