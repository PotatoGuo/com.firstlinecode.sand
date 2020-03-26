package com.firstlinecode.sand.server.ibdr;

import com.firstlinecode.basalt.protocol.core.ProtocolException;
import com.firstlinecode.basalt.protocol.core.stanza.error.NotAcceptable;
import com.firstlinecode.granite.framework.core.annotations.Component;
import com.firstlinecode.granite.framework.core.annotations.Dependency;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.server.framework.things.IDeviceManager;

@Component("default.device.registrar")
public class Registrar implements IDeviceRegistrar {
	@Dependency("device.manager")
	private IDeviceManager deviceManager;
	
	@Override
	public DeviceIdentity register(String deviceId) {
		if (deviceManager.isValid(deviceId))
			throw new ProtocolException(new NotAcceptable(String.format("Invalid device ID '%s'.", deviceId)));

		return deviceManager.register(deviceId);
	}

	@Override
	public void remove(String deviceId) {
		// TODO
	}
	
}