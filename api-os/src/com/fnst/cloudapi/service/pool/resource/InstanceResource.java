package com.fnst.cloudapi.service.pool.resource;

import java.util.HashMap;
import java.util.Map;

import com.fnst.cloudapi.pojo.openstackapi.forgui.Flavor;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Pool;
import com.fnst.cloudapi.pojo.openstackapi.forgui.Stack;
import com.fnst.cloudapi.service.openstackapi.FlavorService;
import com.fnst.cloudapi.service.openstackapi.StackService;
import com.fnst.cloudapi.service.openstackapi.impl.FlavorServiceImpl;
import com.fnst.cloudapi.service.openstackapi.impl.StackServiceImpl;
import com.fnst.cloudapi.service.pool.BasicResource;

public class InstanceResource extends BasicResource {
	private String ResourceType = "OS::Nova::Server";
	private Map<String, String[]> templateParamsSchema;

	public InstanceResource() {
		this.templateParamsSchema = new HashMap<String, String[]>();
		this.templateParamsSchema.put("instance2", new String[] { "image_id", "flavor", "server_name", "network" });

	}

	public String getTemplate() {
		return "instance2";
	}

	public Map<String, String[]> getTemplateParamsSchema() {
		return this.templateParamsSchema;
	}

	public String create(String stackName, Map<String, Object> params, String tokenId) throws Exception {
		// String template = this.getTemplateContent(this.getTemplate(), false);
		String template = this.getTemplateContent("instance2", false);
		// Map<String, String> filterdParmas = this.getFilterdParmas(params);
		Map<String, String> filterdParmas = new HashMap<String, String>();
		// TODO use heat client to create a stack with template and parameters
		StackService stackService = new StackServiceImpl();
		// Map<String, String> files = new HashMap<String, String>();
		// files.put("file://instance2.yaml",
		// this.getTemplateContent("instance2", false));
		Stack stack = stackService.createStack(stackName, filterdParmas, template, null, null, tokenId);
		// TODO fetch stack info and get cpu/mem of instance
		String flavorName = (String) params.get("flavor");
		FlavorService flavorService = new FlavorServiceImpl();
		Flavor flavor = null;
		// flavorService.getFlavor(, tokenId)

		// int cpu = flavor.getVcpus();
		// int ram = flavor.getRam();
		// TODO get pool info from db
		Pool pool = null;
		// int currentCpu = pool.getCores();
		// int currentRam = pool.getRamSize();

		this.updatePool(ResourceAction.CREATE);

		// TODO new Thread
		RefreshStackThread t = new RefreshStackThread(stack.getId(), pool, flavor, "");
		Thread thread = new Thread(t);
		thread.start();
		return null;
	}

	public String get(String stackID, String tokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	public String delete(String stackID, String tokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	public String update(Map<String, String> params, String tokenID) {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<String, String> getFilterdParmas(Map<String, String> params) throws Exception {
		String template = this.getTemplate();
		String[] paramsSchema = this.getTemplateParamsSchema().get(template);
		Map<String, String> filterdParams = new HashMap<String, String>();
		for (String schema : paramsSchema) {
			String param = params.get(schema);
			if (param == null) {
				throw new Exception("param: " + schema + " not found");
			} else {
				filterdParams.put(schema, param);
			}
		}
		return filterdParams;
	}

	public static void main(String[] args) {
		InstanceResource ir = new InstanceResource();
		String template = ir.getTemplateContent(ir.getTemplate(), false);
		System.out.println(template);
		Map<String, String> paramMap = new HashMap<String, String>();
		// paramMap.put("image_id", "22cf53ef-82a8-4603-b203-ebb0bdedb7e3");
		// paramMap.put("flavor", "tiny");
		// paramMap.put("network", "net1");
		// paramMap.put("server_name", "template_test");
		// try {
		// ir.create("sssss12", paramMap, "");
		// Thread.sleep(20000);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }

	}

}

class RefreshStackThread implements Runnable {
	private String stackId;
	private String tokenId;
	private Pool pool;
	private Flavor flavor;

	public RefreshStackThread(String stackId, Pool pool, Flavor flavor, String tokenId) {
		this.stackId = stackId;
		this.tokenId = tokenId;
		this.pool = pool;
		this.flavor = flavor;
	}

	@Override
	public void run() {
		StackService stackService = new StackServiceImpl();
		while (true) {
			Stack stack = null;
			try {
				Thread.sleep(3000);
				stack = stackService.getStack(this.stackId, tokenId);
			} catch (Exception e) {
				continue;
			}
			String status = stack.getStatus();
			if (status.equals("CREATE_COMPLETE")) {
				System.out.println("***** STACK: " + this.stackId + " CREATE_COMPLETE *****");
				stack.setStatus("CREATE_COMPLETE");
				// TODO save stack to db
				break;
			}
			if (status.equals("CREATE_FAILED")) {
				System.out.println("***** STACK: " + this.stackId + " CREATE_FAILED *****");
				stack.setStatus("CREATE_FAILED");
				// TODO save stack to db
				// TODO rollback pool resource
				int cpu = this.flavor.getVcpus();
				int ram = this.flavor.getRam();
				pool.setCores(this.pool.getCores() - cpu);
				pool.setRamsSize(this.pool.getRamSize() - ram);
				break;
			}
			if (status.equals("CREATE_IN_PROGRESS")) {
				System.out.println("***** STACK: " + this.stackId + " CREATE_IN_PROGRESS *****");
				continue;
			}
		}
	}
}
