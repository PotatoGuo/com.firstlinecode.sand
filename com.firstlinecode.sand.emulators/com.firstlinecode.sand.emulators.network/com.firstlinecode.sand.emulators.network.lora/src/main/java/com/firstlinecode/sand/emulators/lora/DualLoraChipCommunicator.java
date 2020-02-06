package com.firstlinecode.sand.emulators.lora;

import com.firstlinecode.sand.client.things.commuication.ICommunicationListener;
import com.firstlinecode.sand.client.things.commuication.ICommunicator;

public class DualLoraChipCommunicator implements ICommunicator<LoraAddress, byte[]> {
	public enum CommunicationMode {
		ADDRESS_CONFIGURATION,
		WORKING
	}
	
	private ILoraNetwork network;
	protected ILoraChip masterChip;
	protected ILoraChip slaveChip;
	protected CommunicationMode mode;
	
	public DualLoraChipCommunicator(ILoraNetwork network, LoraAddress masterAddress, LoraAddress slaveAddress) {
		this.network = network;
		
		masterChip = createLoraChip(masterAddress);
		slaveChip = createLoraChip(slaveAddress);
		
		mode = CommunicationMode.WORKING;
	}
	
	protected ILoraChip createLoraChip(LoraAddress address) {
		return (ILoraChip)network.createChip(address, new LoraChipCreationParams(ILoraChip.Type.HIGH_POWER));
	}
	
	@Override
	public void send(LoraAddress to, byte[] data) {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void received(LoraAddress from, byte[] data) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addCommunicationListener(ICommunicationListener<LoraAddress> listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public LoraAddress getAddress() {
		return getMasterAddress();
	}
	
	public LoraAddress getMasterAddress() {
		return masterChip.getAddress();
	}
	
	public LoraAddress getSlaveAddress() {
		return slaveChip.getAddress();
	}
	
	public ILoraChip getMasterChip() {
		return masterChip;
	}
	
	public ILoraChip getSlaveChip() {
		return slaveChip;
	}
	
	public void setCommunicationMode(CommunicationMode mode) {
		this.mode = mode;
	}
	
	public CommunicationMode getCommunicationMode() {
		return mode;
	}
}
