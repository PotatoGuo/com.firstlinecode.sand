package com.firstlinecode.sand.client.things;

public interface IDevice {
	String getDeviceId();
	String getDeviceType();
	String getDeviceMode();
	String getSoftwareVersion();
	int getBatteryPower();
	void powerOn();
	void powerOff();
	boolean isPowered();
}