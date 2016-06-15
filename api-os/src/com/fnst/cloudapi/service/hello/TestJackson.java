package com.fnst.cloudapi.service.hello;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Instance;

public class TestJackson {
	
	public static  void JsonToJava(){
			
		try {
			
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally  
			Instance ic = mapper.readValue(new File("D:/temptest/instance.json"), Instance.class);
			System.out.println(ic.getId());
			System.out.println(ic.getFloatingIps());	
			Image im= ic.getImage();
			if(im==null){
				System.out.println("im is null");
			}else{
			System.out.println(im.getName());
			}
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
//		mapper.writeValue(new File("user-modified.json"), user); 
		
//		Instance
		
		
	}
	
	
	public static  void JsonToJava2(){
		List<Instance> ll= new ArrayList<Instance>();
		
		try {
			
			ObjectMapper mapper = new ObjectMapper(); // can reuse, share globally  
		
			ll= mapper.readValue(new File("D:/temptest/instance2.json"),new TypeReference<List<Instance>>(){}); 
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		
		System.out.println("ll="+ll.toString());
		
		for(Instance in : ll){
			
			System.out.println("ip="+in.toString());
		}
		
//		mapper.writeValue(new File("user-modified.json"), user); 
		
//		Instance
		
		
	}
	
	
	public static void parseOS(){
		
		try {

			ObjectMapper mapper = new ObjectMapper(); // can reuse, share
														// globally

			JsonNode node = mapper.readTree(new File("D:/temptest/server.json"));
			JsonNode addr = node.path("server").path("addresses");
			int size = addr.size();
			System.out.println("size=" + size);

//			List<String> list = addr.findValuesAsText("addr");
//
//			for (String ip : list) {
//
//				System.out.println("ip=" + ip);
//			}
			
			List<String> list = addr.findValuesAsText("OS-EXT-IPS:type");
			System.out.println("list=" + list.toString());
			if(!list.contains("floating")){
				
				System.out.println("has no floating ip");
				
			}else{
				
//				ArrayList<String> alist = new ArrayList<String>();
				
				Iterator<JsonNode> ir=	addr.elements();
				ArrayList<String> ips = new ArrayList<String>();
				ArrayList<String> floatingips = new ArrayList<String>();
				while(ir.hasNext()){					
					JsonNode oneNetCard=ir.next();
					int size2 =oneNetCard.size();		
					for (int ii=0;ii<size2;ii++){
						String type =oneNetCard.path(ii).path("OS-EXT-IPS:type").textValue();
						String ip   =oneNetCard.path(ii).path("addr").textValue();
						
						System.out.println("type1="+type);
						System.out.println("ip1=" + ip);
						
//						if ("fixed".equals(type)){
//							System.out.println("type fiexed");
//							System.out.println("ip=" + ip);
//							ips.add(ip);
//							
//						}else if ("floating".equals(type)){
//							System.out.println("type floating");
//							System.out.println("ip=" + ip);
//							floatingips.add(ip);
//						}else{
//							
//							System.out.println("type other");
//							System.out.println("ip=" + ip);
//							
//						}
						
                        if ("floating".equals(type)) {
							System.out.println("type floating");
							System.out.println("ip=" + ip);
							floatingips.add(ip);
						} else {
							System.out.println("type other");
							System.out.println("ip=" + ip);
							ips.add(ip);
						}
							
					}
					

					
				}
				
				System.out.println("ips="+ips);
				System.out.println("floatingips="+floatingips);
						
			}
			
			
			

			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	public static void main(String arg[]) {

	//	TestJackson.JsonToJava();

		/*
		 * 1)如果Java类有某一个属性，json里却没有传递的话，转为为Java时，该值为null
		 * 
		 * 2)如果Json里有某一个值，但是却没有Java属性和它对应时，就会报错
		 * com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException:
		 * Unrecognized field "cbl" 解决方法：在Java类里增加此属性
		 * 但是实际上你不需要在这个值的话，可以指定Jackson不解析
		 * 1-@JsonIgnore注解用来忽略某些字段，可以用在Field或者Getter方法上，用在Setter方法时，和Filed效果一样。
		 * 2-@JsonIgnoreProperties(ignoreUnknown = true)，将这个注解写在类上之后，就会忽略类中不存在的字段，可以满足当前的需要。
		 * 3-@JsonIgnoreProperties({"internalId", "secretKey" })这个注解还可以指定要忽略的字段。
		 */

		TestJackson.parseOS();
	}
	
	

}
