package com.fnst.cloudapi.service.openstackapi.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.json.forgui.KeypairJSON;
import com.fnst.cloudapi.pojo.common.TokenOs;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceDetail;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Keypair;
import com.fnst.cloudapi.service.common.impl.CloudConfigAndTokenHandler;
import com.fnst.cloudapi.service.openstackapi.KeypairService;
import com.fnst.cloudapi.util.ParamConstant;
import com.fnst.cloudapi.util.ResponseConstant;
import com.fnst.cloudapi.util.http.HttpClientForOsRequest;
import com.fnst.cloudapi.util.http.RequestUrlHelper;

public class KeypairServiceImpl extends CloudConfigAndTokenHandler implements KeypairService {
	private HttpClientForOsRequest httpClient = null;
	private int ERROR_HTTP_CODE = 400;
	private int NORMAL_HTTP_CODE = 200;

	public KeypairServiceImpl() {
		httpClient = new HttpClientForOsRequest();
	}

	@Override
	public List<Keypair> getKeypairList(Map<String, String> paraMap, String guiTokenId,HttpServletResponse response) throws BusinessException{

		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";// we should get the regioninfo by the
									// guiTokenId

		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/os-keypairs", paraMap);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		Map<String, String> rs = httpClient.httpDoGet(url, headers);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());
		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
        List<Keypair> keypairs = null;
		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			try {
				keypairs = getKeypairs(rs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = httpClient.httpDoGet(url, headers);
			try {
				keypairs = getKeypairs(rs);
			}  catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0006");
		}

		return keypairs;
	}

	@Override
	public Keypair createKeypair(Map<String, String> paraMap, String tokenId,HttpServletResponse response) throws BusinessException {
		// todo 1: 通过guitokenid 取得实际，用户信息
		// AuthService as = new AuthServiceImpl();
		// as.GetTokenOS(guiTokenId);

		TokenOs ot = super.osToken;
		// token should have Regioninfo
		String keyFilePath = "/var/keypairs/key/"; //TODO change it later

		String region = "RegionOne";// we should get the regioninfo by the
									// guiTokenId

		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		url = RequestUrlHelper.createFullUrl(url + "/os-keypairs", null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());

		String body = generateBody(paraMap);

		Map<String, String> rs = httpClient.httpDoPost(url, headers, body);
		// Map<String, String> rs =client.httpDoGet(url, ot.getTokenid());

		int responceCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if(null != response)
		    response.setStatus(responceCode);
		Keypair keypair = null;
		switch (responceCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE:{
			try {
				keypair = genPrivateKey(rs,keyFilePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = httpClient.httpDoPost(url, headers, body);
			try {
				keypair = genPrivateKey(rs,keyFilePath);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0003");
			}
			break;
		}
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0004");
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0003");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_CREATE_0006");
		}
		
		return keypair;
	}

	@Override
	public Keypair getKeypair(Map<String, String> paramMap, String tokenId, HttpServletResponse response)
			throws BusinessException {
		TokenOs ot = super.osToken;
		// token should have Regioninfo

		String region = "RegionOne";// we should get the regioninfo by the
									// guiTokenId
		String url = ot.getEndPoint(TokenOs.EP_TYPE_COMPUTE, region).getPublicURL();
		StringBuilder sb = new StringBuilder();
		sb.append(url);
		sb.append("/os-keypairs/");
		sb.append(paramMap.get(ParamConstant.NAME));
		url = RequestUrlHelper.createFullUrl(sb.toString(), null);

		HashMap<String, String> headers = new HashMap<String, String>();
		headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, ot.getTokenid());
		Map<String, String> rs = httpClient.httpDoGet(url, headers);

		System.out.println("httpcode:" + rs.get("httpcode"));
		System.out.println("jsonbody:" + rs.get("jsonbody"));

		int httpCode = Integer.parseInt(rs.get(ResponseConstant.HTTPCODE));
		if (null != response)
			response.setStatus(httpCode);
		Keypair keypair = null;

		switch (httpCode) {
		case ParamConstant.NORMAL_SYNC_RESPONSE_CODE: {
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode keypairNode = rootNode.path(ResponseConstant.KEYPAIR);
				keypair = getKeypairInfo(keypairNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0003");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0003");
			}
			break;
		}
		case ParamConstant.UN_AUTHHORIZED_RESPONSE_CODE: {
			String tokenid = "";// TODO reget the token id
			headers.put(ParamConstant.OPENSTACK_AUTH_TOKEN, tokenid);
			rs = httpClient.httpDoGet(url, headers);
			ObjectMapper mapper = new ObjectMapper();
			try {
				JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
				JsonNode keypairNode = rootNode.path(ResponseConstant.KEYPAIR);
				keypair = getKeypairInfo(keypairNode);
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0003");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0003");
			}
			break;
		}
		case ParamConstant.BAD_REQUEST_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0003");
		case ParamConstant.NOT_FOUND_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0004");
		case ParamConstant.SERVICE_FORBIDDEN_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0005");
		case ParamConstant.SERVICE_UNAVAILABLE_RESPONSE_CODE:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0002");
		default:
			throw new ResourceBusinessException("CS_COMPUTE_KEYPAIR_GET_0006");
		}

		return keypair;
	}

	private String generateBody(Map<String, String> paraMap) {
		if (null == paraMap || 0 == paraMap.size())
			return "";

		ObjectMapper mapper = new ObjectMapper();
		mapper.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.setSerializationInclusion(Include.NON_EMPTY);
		KeypairJSON keypair = null;
		if (1 == paraMap.size())
			keypair = new KeypairJSON(new Keypair(paraMap.get(ParamConstant.NAME)));
		else
			keypair = new KeypairJSON(
					new Keypair(paraMap.get(ParamConstant.NAME), paraMap.get(ParamConstant.PUBLIC_KEY)));
		String jsonStr = "";
		try {
			jsonStr = mapper.writeValueAsString(keypair);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonStr;
	}

	private Keypair genPrivateKey(Map<String, String> rs,String keyFilePath) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode keypairNode = rootNode.path(ResponseConstant.KEYPAIR);
		int keypairProCount = keypairNode.size();
		if (5 != keypairProCount)
			return null;
		Keypair keypair = new Keypair();
		keypair.setId(keypairNode.path(ResponseConstant.ID).textValue());
		keypair.setName(keypairNode.path(ResponseConstant.NAME).textValue());
		String keyName = keypairNode.path(ResponseConstant.NAME).textValue();
		String privateKey = keypairNode.path(ResponseConstant.PRIVATE_KEY).textValue();
		
		keyFilePath.concat(keyName);
		if (false == saveKeyFile(keyFilePath, privateKey))
			return null;
		keypair.setPrivateKeyPath(keyFilePath.concat("/key.pem"));
		keypair.setPublic_key(keypairNode.path(ResponseConstant.PUBLIC_KEY).textValue());
		keypair.setFingerprint(keypairNode.path(ResponseConstant.FINGERPRINT).textValue());
		keypair.setUser_id(keypairNode.path(ResponseConstant.USER_ID).textValue());
		
		return keypair;
	}
	
	private boolean saveKeyFile(String keyPath, String privateKey) {
		if ("".equals(privateKey))
			return false;
		String filePath = keyPath.concat("/key.pem");
		try {
			// 获得文件对象
			File Keyfile = new File(filePath);
			if (!Keyfile.exists()) {
				if (!Keyfile.getParentFile().exists())
					Keyfile.getParentFile().mkdirs();
				Keyfile.createNewFile();
			}
			FileWriter resultFile = new FileWriter(filePath);
			PrintWriter myFile = new PrintWriter(resultFile);
			myFile.println(privateKey);
			resultFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	private List<Keypair> getKeypairs(Map<String, String> rs) throws JsonProcessingException, IOException{
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.readTree(rs.get(ResponseConstant.JSONBODY));
		JsonNode keypairsNode = rootNode.path(ResponseConstant.KEYPAIRS);
		int keypairsCount = keypairsNode.size();
		if (0 == keypairsCount)
			return null;
		List<Keypair> keypairs = new ArrayList<Keypair>();
		for (int index = 0; index < keypairsCount; ++index) {
			JsonNode keypairNode = keypairsNode.get(index).path(ResponseConstant.KEYPAIR);
			Keypair keypairInfo = getKeypairInfo(keypairNode);
			keypairs.add(keypairInfo);
		}
		return keypairs;
	}
	
	
	private Keypair getKeypairInfo(JsonNode keypairNode) {
		if (null == keypairNode)
			return null;
		Keypair keypairInfo = new Keypair();
		keypairInfo.setId(Integer.toString(keypairNode.path(ResponseConstant.ID).intValue()));
		keypairInfo.setName(keypairNode.path(ResponseConstant.NAME).textValue());
		keypairInfo.setFingerprint(keypairNode.path(ResponseConstant.FINGERPRINT).textValue());
		keypairInfo.setPublic_key(keypairNode.path(ResponseConstant.PUBLIC_KEY).textValue());
		keypairInfo.setUser_id(keypairNode.path(ResponseConstant.USER_ID).textValue());
		keypairInfo.setCreated_at(keypairNode.path(ResponseConstant.CREATED_AT).textValue());
		return keypairInfo;
	}
}
