package com.firstlinecode.sand.server.device;

import com.firstlinecode.basalt.protocol.core.JabberId;
import com.firstlinecode.sand.protocols.core.DeviceIdentity;
import com.firstlinecode.sand.protocols.core.ModeDescriptor;

public interface IDeviceManager {
	void authorize(String deviceId, String authorizer, long validityTime);
	void cancelAuthorization(String deviceId);
	DeviceIdentity register(String deviceId);
	void create(Device device);
	void remove(JabberId jid);
	Device getByDeviceId(String deviceId);
	Device getByDeviceName(String deviceName);
	boolean deviceIdExists(String deviceId);
	boolean deviceNameExists(String deviceName);
	void registerMode(String mode, ModeDescriptor modeDescriptor);
	ModeDescriptor unregisterMode(String mode);
	boolean isRegistered(String deviceId);
	boolean isConcentrator(String mode);
	boolean isActuator(String mode);
	boolean isSensor(String mode);
	boolean isActionSupported(String mode, String actionName);
	boolean isEventSupported(String mode, String eventName);
	Class<?> getActionType(String mode, String actionName);
	Class<?> getEventType(String mode, String eventName);
	boolean isValid(String deviceId);
	String getMode(String deviceId);
}
