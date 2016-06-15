package com.fnst.cloudapi.pojo.openstackapi.forgui;

import java.util.ArrayList;
import java.util.List;

public class InstanceDetail extends Instance {
	
	//private Spec spec;  @TODO

	private List<Image> snapshots = new ArrayList<Image>();

	private List<Volume> volumes = new ArrayList<Volume>();
	
	private List<Network> networks = new ArrayList<Network>();

	private List<Keypair> keypairs = new ArrayList<Keypair>();

	public List<Image> getSnapshots() {
		return snapshots;
	}

	public void setSnapshots(List<Image> snapshots) {
		this.snapshots = snapshots;
	}

	public List<Volume> getVolumes() {
		return volumes;
	}

	public void setVolumes(List<Volume> volumes) {
		this.volumes = volumes;
	}

	public List<Network> getNetworks() {
		return networks;
	}

	public void setNetworks(List<Network> networks) {
		this.networks = networks;
	}

	public List<Keypair> getKeypairs() {
		return keypairs;
	}

	public void setKeypairs(List<Keypair> keypairs) {
		this.keypairs = keypairs;
	}
	
	public void addImage(Image image){
		this.snapshots.add(image);
	}
	
	public void addVolume(Volume volume){
		this.volumes.add(volume);
	}
	
	public void addKeypair(Keypair keypair){
		this.keypairs.add(keypair);
	}
	
	public void addNetwork(Network network){
		this.networks.add(network);
	}

}

