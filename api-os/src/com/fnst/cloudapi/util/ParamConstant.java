package com.fnst.cloudapi.util;

public class ParamConstant {

	public static final String AUTH_TOKEN = "X-ApiAuth-Token";
	public static final String OPENSTACK_AUTH_TOKEN = "X-Auth-Token";
	public static final String LIMIT = "limit";
	public static final String NAME = "name";
	public static final String OWNER = "owner";
	public static final String STATUS = "status";
	public static final String VISIBILITY = "visibility";
    public static final String TENANT_ID = "tenant_id";
	public static final String PUBLIC_KEY = "public_key";
	public static final String DISK = "disk";
	public static final String VOLUME = "volume";
	public static final String CORE_NUMBERS = "core_numbers";
	public static final String MEMORY_SIZE = "memory_size";
	public static final String DISK_INFO = "disk_info";
	public static final String PERFORMANCE_DISK = "performanceDisk";
	public static final String HIGH_PERFORMANCE_DISK = "highPerformanceDisk";
	public static final String CAPACITY_DISK = "capacityDisk";
	public static final String SERVICE_NAME = "service";
	public static final String HOST_NAME = "host_name";
	public static final int TOTAL_RES = 1;
	public static final int USED_RES = 2;
	public static final int FREE_RES = 3;
	public static final int MB = 1024;
	public static final String SUPPORT_IP_VERSION = "4";
	public static final int NORMAL_SYNC_RESPONSE_CODE = 200;
	public static final int NORMAL_CREATE_RESPONSE_CODE = 201;
	public static final int NORMAL_DELETE_RESPONSE_CODE = 204;
	public static final int NORMAL_ASYNC_RESPONSE_CODE = 202;
	public static final int BAD_REQUEST_RESPONSE_CODE = 400;
	public static final int UN_AUTHHORIZED_RESPONSE_CODE = 401;
	public static final int SERVICE_FORBIDDEN_RESPONSE_CODE = 403;
	public static final int NOT_FOUND_RESPONSE_CODE = 404;
	public static final int SERVICE_UNAVAILABLE_RESPONSE_CODE = 503;

	
	/****** network type******/
	public static final String BGP_NETWORK = "bgp_net";
	public static final String TELECOM_NETWORK = "telcom_net";
	public static final String UNICOM_NETWORK = "unicom_net";
	public static final String MOBILE_NETWORK = "mobile_net";
	public static final String NETWORK_ID = "network_id";
	
	public static final String RAM_LIMIT = "ram";
	public static final String FLOATING_IPS_LIMIT = "floating_ips";
	public static final String CORES_LIMIT = "limit";
    
	public static final String ACTIVE_STATUS = "ACTIVE";
	public static final String STORAGE_BACKEND_NAME = "volume_backend_name";
    public static final String RAM = "ram";
    public static final String VCPUS = "vcpus";
    public static final String ID = "id";
    public static final String SWAP = "swap";
    public static final String RXTX_FACTOR = "rxtx_factor";
    public static final String SNAPSHOT_TYPE_IMAGE="snapshot";
    
    public static final String IMAGE_TYPE = "image";
    public static final String SNAPSHOT_TYPE = "snapshot";
    public static final String BLANK_TYPE = "blank";
    public static final String LOCAL_TYPE = "local";
    public static final String IMAGEREF = "imageRef";
    public static final String FLAVORREF = "flavorRef";
    public static final String KEY_NAME = "key_name";
    public static final String AVAILABILITY_ZONE = "availability_zone";
    public static final String MIN_COUNT = "min_count";
    public static final String MAX_COUNT = "max_count";
    public static final String UUID = "uuid";
    public static final String SOURCE_TYPE = "source_type";
    public static final String DESTINATION = "destination_type";
    public static final String BOOT_INDEX = "boot_index";
    public static final String DELETE_ON_TERMINATION = "delete_on_termination";
    public static final String FIXED_IP = "fixed_ip";
    public static final String PORT = "port";
    public static final String SERVER = "server";
    public static final String BLOCK_DEVICE_MAPPING_V2 = "block_device_mapping_v2";
    public static final String NETWORKS = "networks";
    public static final String IP_VERSION = "ip_version";
    public static final String CIDR = "cidr";
    public static final String BASIC_NET = "basic";
    public static final String PRIVATE_NET = "private";
    public static final String PASSWORD_CREDENTIAL = "password";
    public static final String KEYPAIR_CREDENTIAL = "keypair";
    public static final String METADATA = "metadata";
    public static final String VOLUME_TYPE = "volume_type";
    public static final String VOLUME_SIZE = "volume_size";
    public static final String FIXED = "fixed";
    public static final String CREATE_IMAGE_ACTION = "createImage";
    public static final String GET_SNAPSHOT_ACTION = "getSnapshot";
    public static final String TYPE = "type";
    public static final String READ = "read";
    public static final String INSTANCE = "instnace";
    public static final String ROUTER = "router";
    public static final String IMAGES = "images";
    public static final String SUBNET = "subnet";
    
	private ParamConstant(){}
}

