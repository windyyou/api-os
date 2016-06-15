package com.fnst.cloudapi.util;

import java.util.UUID;

public class UUIDHelper {
	
	public static String buildUUIDStr(){
		
		return UUID.randomUUID().toString();
	}
	
	public static UUID  buildUUID(){
		
		return UUID.randomUUID();
	}

}
