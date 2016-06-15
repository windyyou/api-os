package com.fnst.cloudapi.controller.openstackapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fnst.cloudapi.dao.common.FloatingIPMapper;
import com.fnst.cloudapi.dao.common.ImageMapper;
import com.fnst.cloudapi.dao.common.InstanceMapper;
import com.fnst.cloudapi.dao.common.KeypairMapper;
import com.fnst.cloudapi.dao.common.NetworkMapper;
import com.fnst.cloudapi.dao.common.PortMapper;
import com.fnst.cloudapi.dao.common.SubnetMapper;
import com.fnst.cloudapi.dao.common.VolumeMapper;
import com.fnst.cloudapi.exception.BusinessException;
import com.fnst.cloudapi.exception.ResourceBusinessException;
import com.fnst.cloudapi.pojo.common.Util;
import com.fnst.cloudapi.pojo.openstackapi.forgui.FloatingIP;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Image;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Instance;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceConfig;
import com.fnst.cloudapi.pojo.openstackapi.forgui.InstanceDetail;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Keypair;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Network;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Port;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Subnet;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Volume;
import com.fnst.cloudapi.service.openstackapi.InstanceService;
import com.fnst.cloudapi.service.openstackapi.OsApiServiceFactory;
import com.fnst.cloudapi.util.JsonHelper;
import com.fnst.cloudapi.util.ParamConstant;

@RestController
public class InstanceController {
	@Resource
	InstanceMapper instanceMapper;

	@Resource
	ImageMapper imageMapper;

	@Resource
	VolumeMapper volumeMapper;

	@Resource
	KeypairMapper keypairMapper;

	@Resource
	NetworkMapper networkMapper;

	@Resource
	SubnetMapper subnetMapper;

	@Resource
	FloatingIPMapper floatingIpMapper;
	
	@Resource
	PortMapper portMapper;

	/**
	 * get the instance list by parameter and guitoken
	 * 
	 * @param guiToken
	 *            guitokenid
	 * @param limit
	 *            the now to be show
	 * @param name
	 *            the name of instance
	 * @param status
	 *            the status of instance
	 * @param imageid
	 *            the imageid of instance
	 * @return
	 */

	@RequestMapping(value = "/instances", method = RequestMethod.GET)
	public String getIntancesList(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestParam(value = "limit", defaultValue = "") String limit,
			@RequestParam(value = "name", defaultValue = "") String name,
			@RequestParam(value = "status", defaultValue = "") String status,
			@RequestParam(value = "image", defaultValue = "") String imageid, HttpServletResponse response) {

		List<Instance> instancesFromDB = instanceMapper.selectList();
		if (null != instancesFromDB && 0 != instancesFromDB.size()) {
			List<Instance> instancesWithDBInfo = new ArrayList<Instance>();
			for (Instance instance : instancesFromDB) {
				Image image = new Image(instance.getId(), instance.getName());
				instance.setImage(image);
				instancesWithDBInfo.add(instance);
			}
			JsonHelper<List<Instance>, String> jsonHelp = new JsonHelper<List<Instance>, String>();
			return jsonHelp.generateJsonBodyWithEmpty(instancesWithDBInfo);
		}

		Map<String, String> paramMap = null;
		if (!"".equals(limit)) {
			paramMap = new HashMap<String, String>();
			paramMap.put("limit", limit);
		}
		if (!"".equals(name)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put("name", name);
		}
		if (!"".equals(status)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put("status", status);
		}
		if (!"".equals(imageid)) {
			if (paramMap == null)
				paramMap = new HashMap<String, String>();
			paramMap.put("image", imageid);
		}

		// @TODO 1. guitoken should has no defaultValue,if there no token ,bad
		// request

		// @TODO 2. guitoken should be checked, timeout or not

		InstanceService inService = OsApiServiceFactory.getInstanceService();
		try {
			List<InstanceDetail> instances = inService.getInstanceList(paramMap, guiToken, response);
			List<Instance> instancesWithDBInfo = new ArrayList<Instance>();
			for (Instance instance : instances) {
				Instance instanceFromDB = instanceMapper.selectByPrimaryKey(instance.getId());
				if (null == instanceFromDB)
					instanceMapper.insertSelective(instance);// save the
																// instance to
																// local db
				else
					instance.setSourceName(instanceFromDB.getSourceName());
				instancesWithDBInfo.add(instance);
			}
			JsonHelper<List<Instance>, String> jsonHelp = new JsonHelper<List<Instance>, String>();
			return jsonHelp.generateJsonBodySimple(instancesWithDBInfo);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0006");
			return exception.getResponseMessage();
		}

	}

	@RequestMapping(value = "/instances/{id}/volumes", method = RequestMethod.GET)
	public String getIntanceAttachedVolumes(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response) {

		List<Volume> volumesFromDB = volumeMapper.selectListByInstanceId(id);
		if (null != volumesFromDB && 0 != volumesFromDB.size()) {
			JsonHelper<List<Volume>, String> jsonHelp = new JsonHelper<List<Volume>, String>();
			return jsonHelp.generateJsonBodySimple(volumesFromDB);
		}

		Map<String, String> paramMap = new HashMap<String, String>();
		paramMap.put(ParamConstant.ID, id);
		InstanceService inService = OsApiServiceFactory.getInstanceService();
		try {
			List<Volume> volumes = inService.getAttachedVolumes(paramMap, guiToken, response);
			if (null != volumes) {
				for (Volume volume : volumes) {
					if (Util.isSystemVolume(volume.getDevice()))
						continue;
					volume.setInstanceId(id);
					volumeMapper.insertSelective(volume);// save the instance to
															// local db
				}
				JsonHelper<List<Volume>, String> jsonHelp = new JsonHelper<List<Volume>, String>();
				return jsonHelp.generateJsonBodySimple(volumes);
			}

		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0006");
			return exception.getResponseMessage();
		}

		return null;
	}

	@RequestMapping(value = "/instances", method = RequestMethod.POST)
	public String createIntance(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@RequestBody String createBody, HttpServletResponse response) {

		if (null == createBody || createBody.isEmpty()) {
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0003"); // TODO//
																													// change//the/message
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return exception.getResponseMessage();
		}

		ObjectMapper mapper = new ObjectMapper();
		InstanceDetail instanceInfo = null;
		try {
			instanceInfo = mapper.readValue(createBody, InstanceDetail.class);
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0003");
			return exception.getResponseMessage();
		}

		InstanceService inService = OsApiServiceFactory.getInstanceService();
		try {
			Keypair keypairFromDB = keypairMapper.selectByPrimaryKey(instanceInfo.getKeypairId());
			if (null != keypairFromDB) {
				instanceInfo.addKeypair(keypairFromDB);
			}

			InstanceDetail instanceDetail = inService.createInstance(instanceInfo, guiToken, response);

			instanceDetail.setSourceName(instanceInfo.getSourceName());
			instanceDetail.setSourceId(instanceInfo.getSourceId());
			instanceDetail.setCore(instanceInfo.getCore());

			// keypairs
			List<Keypair> keypairs = instanceInfo.getKeypairs();
			if (null != keypairs) {
				for (Keypair keypair : keypairs) {
					keypair.setInstanceId(instanceDetail.getId());
					keypairMapper.insertSelective(keypair);
				}
			}
			// networks
			List<Network> networks = instanceInfo.getNetworks();
			List<String> networksId = new ArrayList<String>();
			if (null != networks) {
				for (Network network : networks) {
					networksId.add(network.getId());
					if (null == networkMapper.selectByPrimaryKey(network.getId()))
						networkMapper.insertSelective(network);
					List<Subnet> subnets = network.getSubnets();
					for (Subnet subnet : subnets) {
						if (null != subnetMapper.selectByPrimaryKey(subnet.getId()))
							subnetMapper.updateByPrimaryKeySelective(subnet);
						else
							subnetMapper.insertSelective(subnet);
					}
				}
			}
			instanceDetail.setNetworkId(Util.listToString(networksId, ','));
			instanceMapper.insertSelective(instanceDetail);

			JsonHelper<InstanceDetail, String> jsonHelp = new JsonHelper<InstanceDetail, String>();
			return jsonHelp.generateJsonBodySimple(instanceDetail);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_INSTANCE_CREATE_0006");
			return exception.getResponseMessage();
		}
	}

	private void setInstanceBaseInfo(InstanceDetail instanceDetail, Instance instance) {
		instanceDetail.setId(instance.getId());
		instanceDetail.setName(instance.getName());
		instanceDetail.setStatus(instance.getStatus());
		instanceDetail.setSourceName(instance.getSourceName());
		instanceDetail.setSourceId(instance.getSourceId());
		instanceDetail.setCore(instance.getCore());
		instanceDetail.setRam(instance.getRam());
		instanceDetail.setFixedIp(instance.getFixedIp());
		instanceDetail.setFloatingIp(instance.getFloatingIp());
		instanceDetail.setCreatedAt(instance.getCreatedAt());
		instanceDetail.setType(instance.getType());
		instanceDetail.setNetworkId(instance.getNetworkId());
	}

	void addSubnetInfo(Network network) {
		if (null == network)
			return;
		String subnetId = network.getSubnetId();
		String[] subnetsId = subnetId.split(",");
		for (int index = 0; index < subnetsId.length; ++index) {
			Subnet subnet = subnetMapper.selectByPrimaryKey(subnetsId[index]);
			if (null == subnet)
				continue; // TODO
			network.addSubnet(subnet);
		}
	}

	void addPortInfo(Network network) {
		if (null == network)
			return;
		String portId = network.getPortId();
		String[] portsId = portId.split(",");
		for (int index = 0; index < portsId.length; ++index) {
			Port port = portMapper.selectByPrimaryKey(portsId[index]);
			if (null == port)
				continue; // TODO
			network.addPort(port);
			;
		}
	}

	void addFloatingIpInfo(Network network) {
		if (null == network)
			return;
		String floatingIpId = network.getFloatingipId();
		String[] floatingIpsId = floatingIpId.split(",");
		for (int index = 0; index < floatingIpsId.length; ++index) {
			FloatingIP floatingIp = floatingIpMapper.selectByPrimaryKey(floatingIpsId[index]);
			if (null == floatingIp)
				continue; // TODO
			network.addFloatingIP(floatingIp);
		}
	}

	@RequestMapping(value = "/instances/{id}", method = RequestMethod.GET)
	public String getInstance(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response) {

		// Firstly get the instance from local db
		InstanceDetail instanceDetail = new InstanceDetail();
		Instance instance = instanceMapper.selectByPrimaryKey(id);
		boolean bNotGet = false;
		if (null != instance) {
			setInstanceBaseInfo(instanceDetail, instance);
			// get images
			List<Image> images = imageMapper.selectListByInstanceId(instance.getId());
			if (null != images && 0 != images.size()) {
				for (Image image : images) {
					instanceDetail.addImage(image);
				}
			} else {
				bNotGet = true;
				// TODO
			}

			// get volumes
			List<Volume> volumes = volumeMapper.selectListByInstanceId(instance.getId());
			if (null != volumes && 0 != volumes.size()) {
				for (Volume volume : volumes) {
					instanceDetail.addVolume(volume);
				}
			} else {
				bNotGet = true;
				// TODO
			}

			// get networks
			String networkId = instanceDetail.getNetworkId();
			if (null != networkId && !networkId.isEmpty()) {
				String[] networksId = networkId.split(",");
				for (int index = 0; index < networksId.length; ++index) {
					Network network = networkMapper.selectByPrimaryKey(networksId[index]);
					if (null == network)
						continue; // TODO
					addSubnetInfo(network);
					addPortInfo(network);
					// addSecurityInfo(network);
					addFloatingIpInfo(network);
					instanceDetail.addNetwork(network);
				}
			}else{
				bNotGet = true;
			}

			// get keypairs
			List<Keypair> keypairs = keypairMapper.selectListByInstanceId(instance.getId());
			if (null != keypairs) {
				for (Keypair keypair : keypairs) {
					instanceDetail.addKeypair(keypair);
				}
			} else {
				// TODO
			}
            if(false == bNotGet){
    			JsonHelper<InstanceDetail, String> jsonHelp = new JsonHelper<InstanceDetail, String>();
    			return jsonHelp.generateJsonBodyWithEmpty(instanceDetail);	
            }
		}

		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.ID, id);
			InstanceService inService = OsApiServiceFactory.getInstanceService();
			instanceDetail = inService.showInstance(paramMap, guiToken, true, instanceDetail, response);
			instanceMapper.updateByPrimaryKeySelective(instanceDetail);

			List<Volume> volumes = instanceDetail.getVolumes();
			if (null != volumes) {
				for (Volume volume : volumes) {
					if (null == volume || Util.isSystemVolume(volume.getDevice()))
						continue;
					volumeMapper.insertSelective(volume);
				}
			}
			List<Keypair> keypairs = instanceDetail.getKeypairs();
			if (null != keypairs) {
				for (Keypair keypair : keypairs) {
					keypairMapper.insertSelective(keypair);
				}
			}

			if (null == instanceDetail.getNetworks() || 0 == instanceDetail.getNetworks().size()) {
				Network network = networkMapper.selectByPrimaryKey(instanceDetail.getId());
				if (null != network) {
					instanceDetail.addNetwork(network);
					addSubnetInfo(network);
					addPortInfo(network);
					// addSecurityInfo(network);
					addFloatingIpInfo(network);
					instanceDetail.addNetwork(network);
				}
			}

			JsonHelper<InstanceDetail, String> jsonHelp = new JsonHelper<InstanceDetail, String>();
			return jsonHelp.generateJsonBodyWithEmpty(instanceDetail);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_INSTANCE_GET_0006");
			return exception.getResponseMessage();
		}
	}

	@RequestMapping(value = "/instances/{id}", method = RequestMethod.DELETE)
	public String deleteInstance(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, HttpServletResponse response) {

		// Firstly get the instance from local db
		try {
			Map<String, String> paramMap = new HashMap<String, String>();
			paramMap.put(ParamConstant.ID, id);
			InstanceService inService = OsApiServiceFactory.getInstanceService();
			inService.deleteInstance(id, guiToken, response);
			instanceMapper.deleteByPrimaryKey(id);
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_INSTANCE_DELETE_0001");
			return exception.getResponseMessage();

		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			return e.getResponseMessage();
		} catch (Exception e) {
			e.printStackTrace();
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_INSTANCE_DELETE_0006");
			return exception.getResponseMessage();
		}
	}
	
	@RequestMapping(value = "/instances/{id}/{action}", method = RequestMethod.POST)
	public String actionInstance(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken,
			@PathVariable String id, @PathVariable String action, @RequestBody String body,
			HttpServletResponse response) {

		if (null == body || body.isEmpty())
			return null; // TODO throw exception
		InstanceService inService = OsApiServiceFactory.getInstanceService();
		try {
			if (ParamConstant.CREATE_IMAGE_ACTION.equals(action)) {
				Image image = inService.createInstanceSnapshot(id, guiToken, body, response);
				if (null == image) {
					response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
					ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_IMAGE_CREATE_0006");
					return exception.getResponseMessage();
				}
				Instance instance = instanceMapper.selectByPrimaryKey(id);
				if (null == instance) {
					Map<String, String> paramMap = new HashMap<String, String>();
					paramMap.put(ParamConstant.ID, id);
					instance = inService.showInstance(paramMap, guiToken, true, null, null);
					if (null != instance)
						image.setSystemName(instance.getSourceName());
				} else {
					image.setSystemName(instance.getSourceName());
				}

				ObjectMapper mapper = new ObjectMapper();
				JsonNode rootNode = mapper.readTree(body);
				JsonNode imageNode = rootNode.path(ParamConstant.CREATE_IMAGE_ACTION);
				String imageName = imageNode.path(ParamConstant.NAME).textValue();
				image.setName(imageName);
				image.setCreatedAt(Util.getCurrentDate());
				imageMapper.insertSelective(image);
				JsonHelper<Image, String> jsonHelp = new JsonHelper<Image, String>();
				return jsonHelp.generateJsonBodyWithEmpty(image);
			}
			// if(ParamConstant.GET_SNAPSHOT_ACTION.equals(action))
			// return inService.createInstanceSnapshot(id, guiToken, body);
		} catch (ResourceBusinessException e) {
			e.printStackTrace();
			return e.getResponseMessage();
		} catch (Exception e) {
			// TODO
			response.setStatus(ParamConstant.BAD_REQUEST_RESPONSE_CODE);
			ResourceBusinessException exception = new ResourceBusinessException("CS_COMPUTE_IMAGE_CREATE_0006");
			return exception.getResponseMessage();
		}

		return "";
	}

	@RequestMapping(value = "/instances/config", method = RequestMethod.GET)
	public InstanceConfig getInstanceConfig(
			@RequestHeader(value = ParamConstant.OPENSTACK_AUTH_TOKEN, defaultValue = "nownoimpl") String guiToken) {

		InstanceService inService = OsApiServiceFactory.getInstanceService();
		InstanceConfig config = inService.getInstanceConfig();

		return config;
	}

}
