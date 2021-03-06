package com.firstlinecode.sand.emulators.light;

import com.firstlinecode.sand.emulators.light.ILight.SwitchState;

public class NotRemoteControlStateException extends Exception {
	private static final long serialVersionUID = -6203531719787698381L;
	
	private SwitchState switchState;
	
	public NotRemoteControlStateException(SwitchState switchState) {
		this.switchState = switchState;
	}
	
	public SwitchState getSwitchState() {
		return switchState;
	}
}
