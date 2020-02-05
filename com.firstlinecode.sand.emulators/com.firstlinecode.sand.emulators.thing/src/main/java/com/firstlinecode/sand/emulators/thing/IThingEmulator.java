package com.firstlinecode.sand.emulators.thing;

import java.io.Externalizable;

import com.firstlinecode.sand.client.things.IThing;
import com.firstlinecode.sand.client.things.commuication.ICommunicationChip;

public interface IThingEmulator extends IThing, Externalizable {
	void setCommunicationChip(ICommunicationChip<?> communicationChip);
	void setDeviceId(String deviceId);
	void setBatteryPower(int batteryPower) ;
	void powerChanged(PowerEvent event);
	void reset();
	AbstractThingEmulatorPanel getPanel();
}
