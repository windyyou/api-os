package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.json.forgui.InstanceJSON;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.common.Util;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Flavor;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceConfig;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceDetail;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Keypair;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Volume;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.FlavorService;
import com.fnst.cloudapi.service.openstackapi.ImageService;
import com.fnst.cloudapi.service.openstackapi.InstanceService;
import com.fnst.cloudapi.service.openstackapi.KeypairService;
import com.fnst.cloudapi.service.openstackapi.NetworkService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.service.openstackapi.SubnetService;
import com.fnst.cloudapi.service.openstackapi.VolumeService;
import com.fnst.cloudapi.util.Message;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class InstanceServiceImpl extends CloudConfigAndTokenHandler implements InstanceService {

	private HttpClientForOsRequest client = null;
	private int NORMAL_ASYNC_RESPONSE_CODE = 202;

	public InstanceServiceImpl() {

		this.client = new HttpClientForOsRequest();

	}

	@Override
	public List<InstanceDetail> getInstanceList(Map<String, String> paraMap, String guiTokenId,HttpServletResponse response) throws BusinessException{

		// todo 1: 閫氳繃guitokenid 鍙栧緱瀹為檯锛岀敤鎴蜂俊鎭�
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/servers/detail", paraMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(url, headers);
		if (null == rs || 0 == rs.size())
			return null;

		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());

		System.out.println("httpcode:" + rs.get("httpcode"));
		System.out.println("jsonbody:" + rs.get("jsonbody"));
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		InstanceDetail instance = null;
		List<InstanceDetail> instances = new ArrayList<InstanceDetail>();
		
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode serversNode = rootNode.path(ResponseConstant.INSTANCES);
				int size = serversNode.size();
				for(int i = 0; i < size; ++i){
					instance = getInstanceInfo(serversNode.get(i),guiTokenId,false);
					if(null != instance)
					   instances.add(instance);
				}
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode serversNode = rootNode.path(ResponseConstant.INSTANCES);
				int size = serversNode.size();
				for(int i = 0; i < size; ++i){
					instance = getInstanceInfo(serversNode.get(i),guiTokenId,false);
					instances.add(instance);
				}
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0006");
		}
		
		return instances;
	}

//	private Flavor srhFlavor(InstanceDetail instanceInfoFromDB,String tokenId){
//		if(null == instanceInfoFromDB.getCore() || instanceInfoFromDB.getCore().isEmpty())
//			return null;
//		if(null == instanceInfoFromDB.getRam() || instanceInfoFromDB.getRam().isEmpty())
//			return null;
//		int vcpus = Integer.parseInt(instanceInfoFromDB.getCore());
//		int ram   = Integer.parseInt(instanceInfoFromDB.getRam());
//		FlavorService flavorService = OsApiServiceFactory.getFlavorService();
//		List<Flavor> list = flavorService.getFlavorList(null, tokenId);
//		if (null != list) {
//			for (Flavor flavor : list) {
//				if (flavor.getRam() == ram && flavor.getVcpus() == vcpus)
//					return flavor;
//			}
//		}
//		return null;
//	}
	
	private InstanceDetail getInstanceDetailInfo(JsonNode serverNode, String tokenId, Boolean details,InstanceDetail instanceInfoFromDB) throws BusinessException {
		
		if (null == serverNode)
			return null;
		InstanceDetail instanceDetail = new InstanceDetail();
		instanceDetail.setId(serverNode.path(ResponseConstant.ID).textValue());
		instanceDetail.setName(serverNode.path(ResponseConstant.NAME).textValue());
		instanceDetail.setStatus(serverNode.path(ResponseConstant.STATUS).textValue());
		instanceDetail.setCreatedAt(serverNode.path(ResponseConstant.CREATED).textValue());
		instanceDetail.setType(serverNode.path(ResponseConstant.OS_EXT_AZ_AVAILABILITY_ZONE).textValue());
		if (false == details)
			return instanceDetail;

		//image
		if(null == instanceInfoFromDB){
			JsonNode imageNode = serverNode.path(ResponseConstant.IMAGE);
			if (null != imageNode && (null != imageNode.path(ResponseConstant.ID).textValue() && !imageNode.path(ResponseConstant.ID).textValue().isEmpty())) {
				instanceDetail.setSourceId(imageNode.path(ResponseConstant.ID).textValue());
				String imageName = ""; // TODO get the image name
				if (imageName.isEmpty()) {
					ImageService imgService = OsApiServiceFactory.getImageService();
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put(ParamConstant.ID, instanceDetail.getSourceId());
					Image image = imgService.getImage(paramMap, tokenId, null);
					instanceDetail.setImage(image);
					instanceDetail.setSourceName(image.getName());
				}
			}
		}else{
			Image image = new Image(instanceInfoFromDB.getSourceId(),instanceInfoFromDB.getSourceName());
			instanceDetail.setImage(image);
			instanceDetail.setSourceId(instanceInfoFromDB.getSourceId());
			instanceDetail.setSourceName(instanceInfoFromDB.getSourceName());
		}
		
		
		//flavor
		if(instanceInfoFromDB == null){
			JsonNode flavorNode = serverNode.path(ResponseConstant.FLAVOR);
			if (null != flavorNode) {
				String flavorId = flavorNode.path(ResponseConstant.ID).textValue();
				FlavorService flavorService = OsApiServiceFactory.getFlavorService();
				Map<String, String> paramMap = new HashMap<String, String>();
				paramMap.put(ParamConstant.ID, flavorId);
				Flavor flavor = flavorService.getFlavor(paramMap, tokenId);
				if (null != flavor) {
					instanceDetail.setCore(Integer.toString(flavor.getVcpus()));
					instanceDetail.setRam(Integer.toString(flavor.getRam()));
				}
			}
		}else{
			instanceDetail.setCore(instanceInfoFromDB.getCore());
			instanceDetail.setRam(instanceInfoFromDB.getRam());
		}
		
		
		//metadata
		JsonNode metadataNode = serverNode.path(ResponseConstant.METADATA);
		if (null != metadataNode) {
			instanceDetail.setVolumeType(metadataNode.path(ResponseConstant.VOLUME_TYPE).textValue());
		}
		
		//keypair
		String keypairName = serverNode.path(ResponseConstant.KEY_NAME).textValue();
		if(null != keypairName && !keypairName.isEmpty()){
			KeypairService kpService = OsApiServiceFactory.getkeypairService();
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.NAME,keypairName);
			Keypair keypair = kpService.getKeypair(paramMap, tokenId, null);
			if(null != keypair)
			    instanceDetail.addKeypair(keypair);
		}
		
		//ip addresses
		List<String> fixedipList = new ArrayList<String>();
		List<String> floatingiplist = new ArrayList<String>();
		JsonNode addressesNode = serverNode.path(ResponseConstant.ADDRESSES);
		if (null != addressesNode) {
			Iterator<Entry<String, JsonNode>> elements = addressesNode.fields();
			while (elements.hasNext()) {
				Entry<String, JsonNode> node = elements.next();
				JsonNode networks = node.getValue();
				int size = node.getValue().size();
				for (int index = 0; index < size; ++index) {
					JsonNode network = networks.get(index);
					String ipAddress = network.path(ResponseConstant.ADDR).textValue();
				//	String version = network.path(ResponseConstant.VERSION).textValue();
					String ipType = network.path(ResponseConstant.EXT_IP_TYPE).textValue();
				//	String mac = network.path(ResponseConstant.MAC_ADDR).textValue();
					if (ParamConstant.FIXED.equals(ipType))
						fixedipList.add(ipAddress);
					else
						floatingiplist.add(ipType);
				}
			}
			instanceDetail.setIps(fixedipList);
			instanceDetail.setFloatingIps(floatingiplist);
		}
		
		
       //snapshots //TODO

		//volumes
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamConstant.ID,instanceDetail.getId());
		List<Volume> volumes = getAttachedVolumes(paramMap,tokenId,null);
		instanceDetail.setVolumes(volumes);

		// networks


		return instanceDetail;
	}

	@Override
	public void deleteInstance(String id,String guiTokenId,HttpServletResponse response) throws BusinessException{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/servers/");
		sb.append(id);
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoDelete(sb.toString(), headers);
		if (null == rs || 0 == rs.size())
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_DELETE_0003");

		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		switch (httpCode) {
		case ParamConstant.NORMAL_DELETE_RESPONSE_CODE: {
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoDelete(url.toString(), headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0006");
		}	
		return ;
	}
	
	@Override
	public InstanceDetail showInstance(Map<String, String> paramMap, String guiTokenId, Boolean details,
			InstanceDetail instanceInfoFromDB,HttpServletResponse response) throws BusinessException {

		// todo 1: 閫氳繃guitokenid 鍙栧緱瀹為檯锛岀敤鎴蜂俊鎭�
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/servers/");
		sb.append(paramMap.get(ParamConstant.ID));

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(sb.toString(), headers);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());

		System.out.println("httpcode:" + rs.get("httpcode"));
		System.out.println("jsonbody:" + rs.get("jsonbody"));

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);

		InstanceDetail instanceDetail = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode serverNode = rootNode.path(ResponseConstant.INSTANCE);
				instanceDetail = getInstanceDetailInfo(serverNode, guiTokenId, details,instanceInfoFromDB);
			}  catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if (null != response)
				response.setStatus(httpCode);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode serverNode = rootNode.path(ResponseConstant.INSTANCE);
				instanceDetail = getInstanceDetailInfo(serverNode, guiTokenId, details,instanceInfoFromDB);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0006");
		}
		return instanceDetail;

	}

	public List<Volume>  getAttachedVolumes(Map<String,String> paramMap,String tokenId,HttpServletResponse response) throws BusinessException
	{
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";
		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/servers/");
		sb.append(paramMap.get(ParamConstant.ID));
		sb.append("/os-volume_attachments");
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = client.httpDoGet(sb.toString(), headers);
		
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		
		List<Volume> volumes= new ArrayList<Volume>();
		Volume volume = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumesNode = rootNode.path(ResponseConstant.VOLUMEATTACHMENTS);
				int size = volumesNode.size();
				for(int i = 0; i < size; ++i){
					if(Util.isSystemVolume(volumesNode.get(i).path(ResponseConstant.DEVICE).textValue())) //Skip the system volume
						continue;
					volume = getVolumeInfo(volumesNode.get(i),tokenId);
					if(null == volume)
						continue;
					volumes.add(volume);
				}
			} catch(BusinessException e){
				throw e;
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoGet(url, headers);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode volumesNode = rootNode.path(ResponseConstant.VOLUMEATTACHMENTS);
				int size = volumesNode.size();
				for(int i = 0; i < size; ++i){
					volume = getVolumeInfo(volumesNode.get(i),tokenId);
					volumes.add(volume);
				}
			}catch(BusinessException e){
				throw e;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_VOLUME_GET_0006");
		}
		return volumes;
	}
	
	private Volume getVolumeInfo(JsonNode volumeNode,String tokenId) throws BusinessException{
		if(null == volumeNode)
			return null;
		
		VolumeService volumeService = OsApiServiceFactory.getVolumeService();
		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamConstant.ID, volumeNode.path(ResponseConstant.ID).textValue());
	    Volume volumeDetail = volumeService.getVolume(paramMap, tokenId, null);
	    if(null == volumeDetail)
	    	return null;
	    
	    volumeDetail.setInstanceId(volumeNode.path(ResponseConstant.SERVERID).textValue());
	    volumeDetail.setDevice(volumeNode.path(ResponseConstant.DEVICE).textValue());
		return volumeDetail;
	}
	
	private Map<String, Map<String, String>> buildInstanceParam(InstanceDetail instanceInfo, String guiTokenId) throws BusinessException{
		
		Map<String, Map<String, String>> instanceParamMap = new HashMap<String, Map<String, String>>();
		String name = instanceInfo.getName();
		String source_type = instanceInfo.getSourceType();
		String source_id = instanceInfo.getSourceId();
		String vcpus = instanceInfo.getCore(); // TODO change it later
		String ram = instanceInfo.getRam();
		String disk = instanceInfo.getVolumeSize();
		String volume_type = instanceInfo.getVolumeType();
		String min_count = "1";
		String max_count = instanceInfo.getQuantity();
		String network_type = instanceInfo.getNetworkType();
		String subnet_id = "";
		Boolean bBasicNetwork = false; // TODO change it later
		if (ParamConstant.BASIC_NET.equals(network_type))
			bBasicNetwork = true;
		else
			subnet_id = instanceInfo.getSubnet();
		String credential_type = instanceInfo.getCredentialType();
		String username = "";
		String password = "";
		String keypairName = "";
		if (ParamConstant.PASSWORD_CREDENTIAL.equals(credential_type)) {
			username = instanceInfo.getUsername();
			password = instanceInfo.getPassword();
		} else {
			keypairName = instanceInfo.getKeypairName();
			Map<String,String> paramMap = new HashMap<String,String>();
	        paramMap.put(ParamConstant.NAME, keypairName); 
	        KeypairService resService = OsApiServiceFactory.getkeypairService();
	 		Keypair keypair = resService.getKeypair(paramMap, guiTokenId, null);
	 		instanceInfo.addKeypair(keypair);	
		}

		String flavorRef = getFlavor(guiTokenId, vcpus, ram, disk);
		if (null == flavorRef)
			return null; // TODO maybe throw exception
        
		instanceParamMap.put(ParamConstant.SERVER, new HashMap<String, String>());
		instanceParamMap.get(ParamConstant.SERVER).put(ParamConstant.NAME, name);
		instanceParamMap.get(ParamConstant.SERVER).put(ParamConstant.FLAVORREF, flavorRef);
		instanceParamMap.get(ParamConstant.SERVER).put(ParamConstant.IMAGEREF, source_id);
		instanceParamMap.get(ParamConstant.SERVER).put(ParamConstant.KEY_NAME, keypairName);
		// instanceParamMap.get(ParamConstant.SERVER).put(ParamConstant.AVAILABILITY_ZONE,
		// availability_zone);
		instanceParamMap.get(ParamConstant.SERVER).put(ParamConstant.MIN_COUNT, min_count);
		instanceParamMap.get(ParamConstant.SERVER).put(ParamConstant.MAX_COUNT, max_count);

		String network_id = "";
		Map<String, String> networkInfo = getNetInfo(bBasicNetwork, subnet_id, guiTokenId,instanceInfo);
		if (null == networkInfo)
			return null; // TODO throw exception
		instanceParamMap.put(ParamConstant.NETWORKS, networkInfo);
        
		// List<String> metadataValues = null; // TODO
		// if (null != volume_type && !volume_type.isEmpty()) {
		// metadataValues = new ArrayList<String>();
		// metadataValues.add(volume_type);
		// }

		Map<String, String> blockDeviceInfo = getBlockDeviceInfo(source_type, source_id, disk, "0");
		if (null != blockDeviceInfo)
			instanceParamMap.put(ParamConstant.BLOCK_DEVICE_MAPPING_V2, blockDeviceInfo);

		return instanceParamMap;
	}

	private InstanceDetail getInstanceInfo(JsonNode instanceNode, String guiTokenId,Boolean details) throws BusinessException{
		try {
			if (null == instanceNode)
				return null;
			if(false == details){
				InstanceDetail instanceDetail = new InstanceDetail();
				instanceDetail.setId(instanceNode.path(ResponseConstant.ID).textValue());
				instanceDetail.setName(instanceNode.path(ResponseConstant.NAME).textValue());
				instanceDetail.setStatus(instanceNode.path(ResponseConstant.STATUS).textValue());
				instanceDetail.setCreatedAt(instanceNode.path(ResponseConstant.CREATED).textValue());
				instanceDetail.setType(instanceNode.path(ResponseConstant.OS_EXT_AZ_AVAILABILITY_ZONE).textValue());
				
				//image
				JsonNode imageNode = instanceNode.path(ResponseConstant.IMAGE);
				if (null != imageNode) {
					String imageId = imageNode.path(ResponseConstant.ID).textValue();
					if(null == imageId || imageId.isEmpty())
						return instanceDetail;;
					instanceDetail.setSourceId(imageId);
					String imageName = ""; // TODO get the image name
					if (imageName.isEmpty()) {
						ImageService imgService = OsApiServiceFactory.getImageService();
						Map<String, String> paramMap = new HashMap<String, String>();
						paramMap.put(ParamConstant.ID, instanceDetail.getSourceId());
						try{
							Image image = imgService.getImage(paramMap, guiTokenId, null);
							instanceDetail.setImage(image);
							instanceDetail.setSourceName(image.getName());
						}catch(ResourceBusinessException e){
							// TODO Auto-generated catch block
							e.printStackTrace();
							return instanceDetail; //TODO
						}	
					}
				}
				return instanceDetail;
			}
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.ID, instanceNode.path(ResponseConstant.ID).textValue());
			return showInstance(paramMap, guiTokenId, false, null,null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public InstanceDetail createInstance(InstanceDetail instanceInfo, String guiTokenId, HttpServletResponse response)
			throws BusinessException {

		if (null == instanceInfo)
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0003"); // TODO throw exception
		// todo 1: 閫氳繃guitokenid 鍙栧緱瀹為檯锛岀敤鎴蜂俊鎭�
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);
		TokenOs ot = super.osToken;

		String region = "RegionOne"; // we should get the regioninfo by the guiTokenId

		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/servers", null);

		// SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// //TODO change it later
		// String name = null != paraMap.get(ParamConstant.NAME) ?
		// paraMap.get(ParamConstant.NAME) : String.format("Instance_Name_%s",
		// df.format(new Date()));
		// String imageRef = null != paraMap.get(ParamConstant.IMAGEREF) ?
		// paraMap.get(ParamConstant.IMAGEREF) :
		// "8d77138e-fd3b-465a-b877-df0f2d7ea0b5"; //TODO change it later
		// String key_name = paraMap.get(ParamConstant.KEY_NAME);
		// String availability_zone =
		// paraMap.get(ParamConstant.AVAILABILITY_ZONE);

		Map<String, Map<String, String>> instanceParamMap = buildInstanceParam(instanceInfo, guiTokenId);

		List<String> metadataValues = null; // TODO
		if (null != instanceInfo.getVolumeType() && !instanceInfo.getVolumeType().isEmpty()) {
			metadataValues = new ArrayList<String>();
			metadataValues.add(instanceInfo.getVolumeType());
		}

		List<String> names = new ArrayList<String>();
		String createBoay = generateBody(instanceParamMap, names, metadataValues);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());
		Map<String, String> rs = client.httpDoPost(url, headers, createBoay);

		int responceCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		    response.setStatus(responceCode);
		InstanceDetail instanceDetail = null;
		switch (responceCode) {
		case ParamConstant.NORMAL_ASYNC_RESPONSE_CODE:{
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode;
			try {
				rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode instanceNode = rootNode.path(ResponseConstant.INSTANCE);
				instanceDetail = getInstanceInfo(instanceNode, guiTokenId,true);	
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(url, headers, createBoay);
			ObjectMapper mapper = new ObjectMapper();
			JsonNode rootNode;
			try {
				rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode instanceNode = rootNode.path(ResponseConstant.INSTANCE);
				instanceDetail = getInstanceInfo(instanceNode, tokenid,true);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0003");
			}
			break;
		}
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0004");
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0003");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0006");
		}

		return instanceDetail;
	}

	@Override
	public Image createInstanceSnapshot(String instanceId, String guiToken, String body,HttpServletResponse response) throws BusinessException {
		// todo 1: 閫氳繃guitokenid 鍙栧緱瀹為檯锛岀敤鎴蜂俊鎭�
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";

		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/servers/");
		sb.append(instanceId);
		sb.append("/action");
		
		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());
		Map<String, String> rs = client.httpDoPost(sb.toString(), headers,body);

		// System.out.println("httpcode:" + rs.get("httpcode"));
		// System.out.println("jsonbody:" + rs.get("jsonbody"));
		int responceCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		String location = rs.get(ResponseConstant.LOCATION);
		if(null != response)
		    response.setStatus(responceCode);
		switch (responceCode) {
		case ParamConstant.NORMAL_ASYNC_RESPONSE_CODE:{
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = client.httpDoPost(sb.toString(), headers,body);
			responceCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
			if(ParamConstant.NORMAL_ASYNC_RESPONSE_CODE != responceCode)
				return null;
			location = rs.get(ResponseConstant.LOCATION);
			if(null != response)
			    response.setStatus(responceCode);
			break;
		}
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_CREATE_0004");
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_CREATE_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_IMAGE_CREATE_0006");
		}

       Image image = new Image();
       image.setInstanceId(instanceId);
       image.setId(Util.getImageIdFromLocation(location));
       return image;
	}


	private Image getImageInfo(String imageid, TokenOs token) {
		Image img = null;
		String url = token.getEndPoint(TokenOs.EP_TYPE_IMAGE).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/v2/images/" + imageid, null);

		Map<String, String> rs = this.client.httpDoGet(url, token.getTokenid());
		if (Integer.parseInt(rs.get("httpcode")) > 400) {

			System.out.println("wo cha:request failed");

		} else {

			ObjectMapper mapper = new ObjectMapper();
			try {
				img = new Image();
				JsonNode rootNode = mapper.readTree(rs.get("jsonbody"));
				img.setId(imageid);
				img.setName(rootNode.path("name").textValue());

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

		return img;
	}

	private String generateBody(Map<String, Map<String, String>> paramMap, List<String> names,
			List<String> metadataValues) {
		if (null == paramMap || 0 == paramMap.size())
			return "";

		Map<String, String> serverMap = paramMap.get(ParamConstant.SERVER);
		if (null == serverMap)
			return "";

		String name = serverMap.get(ParamConstant.NAME);
		String imageRef = "";// serverMap.get(ParamConstant.IMAGEREF) ;
		String flavorRef = serverMap.get(ParamConstant.FLAVORREF);
		String key_name = serverMap.get(ParamConstant.KEY_NAME);
		String availability_zone = serverMap.get(ParamConstant.AVAILABILITY_ZONE);
		Integer min_count = null != serverMap.get(ParamConstant.MIN_COUNT)
				? Integer.parseInt(serverMap.get(ParamConstant.MIN_COUNT)) : 1;
		Integer max_count = null != serverMap.get(ParamConstant.MAX_COUNT)
				? Integer.parseInt(serverMap.get(ParamConstant.MAX_COUNT)) : 1;
		InstanceJSON instanceJSON = new InstanceJSON(name, imageRef, flavorRef, key_name, availability_zone, min_count,
				max_count);

		Map<String, String> blockDeviceMap = paramMap.get(ParamConstant.BLOCK_DEVICE_MAPPING_V2);
		if (null != blockDeviceMap) {
			String uuid = blockDeviceMap.get(ParamConstant.UUID);
			String source_type = blockDeviceMap.get(ParamConstant.SOURCE_TYPE);
			String destination_type = blockDeviceMap.get(ParamConstant.DESTINATION);
			Integer boot_index = null != blockDeviceMap.get(ParamConstant.BOOT_INDEX)
					? Integer.parseInt(blockDeviceMap.get(ParamConstant.BOOT_INDEX)) : null;
			Integer size = null != blockDeviceMap.get(ParamConstant.VOLUME_SIZE)
					? Integer.parseInt(blockDeviceMap.get(ParamConstant.VOLUME_SIZE)) : null;
			Boolean delete_on_termination = null != blockDeviceMap.get(ParamConstant.DELETE_ON_TERMINATION)
					? Boolean.parseBoolean(blockDeviceMap.get(ParamConstant.DELETE_ON_TERMINATION)) : false;
			instanceJSON.createBlock_device_mapping_v2(uuid, source_type, destination_type, boot_index, size,
					delete_on_termination);
		}

		Map<String, String> netMap = paramMap.get(ParamConstant.NETWORKS);
		if (null != netMap) {
			String net_uid = netMap.get(ParamConstant.UUID);
			String fixed_ip = netMap.get(ParamConstant.FIXED_IP);
			String port = netMap.get(ParamConstant.PORT);
			instanceJSON.createNetworks(net_uid, fixed_ip, port);
		}

		if (null != metadataValues) {
			instanceJSON.createMetadata(metadataValues);
		}

		instanceJSON.createSecurity_groups(names);

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(instanceJSON);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}

	private String getFlavor(String guiToken, String flavor_vcpus, String flavor_ram, String flavor_disk) {

		int disk = Integer.parseInt(flavor_disk); // TODO change it later
		int vcpus = Integer.parseInt(flavor_vcpus);
		int ram = Integer.parseInt(flavor_ram);
		int id = 0;
		// First get the flavor from the local DB
		// if not, get the flavor from openstack
		FlavorService flavorService = OsApiServiceFactory.getFlavorService();
		List<Flavor> list = flavorService.getFlavorList(null, guiToken);
		Flavor decidedFlavor = null;
		if (null != list) {
			for (Flavor flavor : list) {
				if (flavor.getDisk() == disk && flavor.getRam() == ram && flavor.getVcpus() == vcpus)
					return flavor.getId();
				int flavor_id = Integer.parseInt(flavor.getId());
				if (id < flavor_id)
					id = flavor_id;
			}
		}

		// if can not find flavor,create the flavor
		Map<String, String> flavorParamMap = new HashMap<String, String>();
		String name = String.format("%s_%s_%s_Flavor", vcpus, ram, disk);
		flavorParamMap.put(ParamConstant.NAME, name);
		flavorParamMap.put(ParamConstant.ID, Integer.toString(++id));
		flavorParamMap.put(ParamConstant.RAM, Integer.toString(ram));
		flavorParamMap.put(ParamConstant.VCPUS, Integer.toString(vcpus));
		flavorParamMap.put(ParamConstant.DISK, Integer.toString(disk));
		decidedFlavor = flavorService.createFlavor(flavorParamMap, guiToken);
		if (null == decidedFlavor)
			return null;
		// TODO save the decidedFlavor to the local db
		return decidedFlavor.getId();
	}

	private Map<String, String> getNetInfo(Boolean bBasicNetwork, String subnet_id, String guiTokenId,InstanceDetail instanceInfo) throws BusinessException{

		if (true == bBasicNetwork) {
			// first, check the whether does the tenant network exist
			// if not,create the tenant network
			NetworkService nwService = OsApiServiceFactory.getNetworkService();
			Network network = nwService.createNetwork(null, guiTokenId,null);
			if (null == network)
				throw new ResourceBusinessException("CS_NETWORK_CREATE_0003"); // TODO throw exception
			String network_id = network.getId();

			SubnetService subnetService = OsApiServiceFactory.getSubnetService();
			Map<String, String> subnetParamMap = new HashMap<String, String>();
			subnetParamMap.put(ParamConstant.NETWORK_ID, network_id);
			subnetParamMap.put(ParamConstant.IP_VERSION, ParamConstant.SUPPORT_IP_VERSION); // TODO change it later
			Subnet subnet = subnetService.createSubnet(Util.getCreateBody(ParamConstant.SUBNET, subnetParamMap), guiTokenId,null);
			if (null == subnet)
				throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0003"); // TODO throw exception
            
			network.addSubnet(subnet);
			instanceInfo.addNetwork(network);
			
			Map<String, String> netParamMap = new HashMap<String, String>();
			netParamMap.put(ParamConstant.UUID, network_id);

			return netParamMap;
		} else {
			if (null == subnet_id || "".equals(subnet_id))
				return null;
			// get the network_id from local db by the subnet_name;
			// if not get the network_id from openstack
			SubnetService subnetService = OsApiServiceFactory.getSubnetService();
			Subnet subnet = subnetService.getSubnet(subnet_id, guiTokenId,null);
			if (null == subnet)
				throw new ResourceBusinessException("CS_NETWORK_SUBNET_CREATE_0003"); // TODO throw exception
			// TODO save the subnet to local db
			Network network =  new Network();
			network.setId(subnet.getNetwork_id());
			network.addSubnet(subnet);
			instanceInfo.addNetwork(network);
			
			Map<String, String> netParamMap = new HashMap<String, String>();
			// netParamMap.put(ParamConstant.NETWORKS, new
			// HashMap<String,String>());
			netParamMap.put(ParamConstant.UUID, subnet.getNetwork_id());

			return netParamMap;
		}
	}

	private Map<String, String> getBlockDeviceInfo(String source_type, String sourceId, String diskSize,
			String bootIndex) throws BusinessException{

		if (null == source_type || source_type.isEmpty())
			throw new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0003");

		Map<String, String> blodkDeviceParamMap = new HashMap<String, String>();
		blodkDeviceParamMap.put(ParamConstant.UUID, sourceId);
		blodkDeviceParamMap.put(ParamConstant.SOURCE_TYPE, source_type);
		blodkDeviceParamMap.put(ParamConstant.DESTINATION, ParamConstant.VOLUME);
		blodkDeviceParamMap.put(ParamConstant.VOLUME_SIZE, diskSize);
		blodkDeviceParamMap.put(ParamConstant.BOOT_INDEX, bootIndex);

		return blodkDeviceParamMap;

	}

	@Override
	public InstanceConfig getInstanceConfig() {
		// TODO I think unitPrice should save in db
		InstanceConfig config = new InstanceConfig();

		Map<String, Object> core = new HashMap<>();
		core.put("core", new Integer[] { 1, 2, 4, 8, 12, 16 });
		core.put("unitPrice", 0.25);

		Map<String, Object> ram = new HashMap<>();
		ram.put("size", new Integer[] { 1024, 2048, 4096, 8192, 12288, 16384, 24576, 32768, 40960 });
		ram.put("unitPrice", 0.15);

		Map<String, Object> volumeType1 = new HashMap<>();
		volumeType1.put("id", "qwerqwer123412341234");
		volumeType1.put("name", "performance");
		volumeType1.put("unitPrice", 0.20);

		Map<String, Object> volumeType2 = new HashMap<>();
		volumeType2.put("id", "qwerqwer123412341233");
		volumeType2.put("name", "capacity");
		volumeType2.put("unitPrice", 0.05);

		Map<String, Object> volume = new HashMap<>();
		volume.put("size", 20);
		volume.put("type", new Object[] { volumeType1, volumeType2 });
		
		Map<String, String> instanceType1 = new HashMap<>();
		instanceType1.put("id", "qwerqwer123412341266");
		instanceType1.put("name", "performance");

		Map<String, String> instanceType2 = new HashMap<>();
		instanceType2.put("id", "qwerqwer123412341267");
		instanceType2.put("name", "highPerformance");
		
		List<Object> instanceType = new ArrayList<>();
		instanceType.add(instanceType1);
		instanceType.add(instanceType2);
		config.setCore(core);
		config.setRam(ram);
		config.setVolume(volume);
		config.setInstanceType(instanceType);
		return config;
	}

}
