package com.firstlinecode.sand.emulators.gateway;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

import com.firstlinecode.sand.emulators.thing.IThing;

public class ThingInfo implements Externalizable {
	private int layer;
	private int x;
	private int y;
	private boolean selected;
	private IThing thing;
	
	public ThingInfo() {}
	
	public ThingInfo(int layer, int x, int y, boolean selected, IThing thing) {
		this.layer = layer;
		this.x = x;
		this.y = y;
		this.selected = selected;
		this.thing = thing;
	}
	
	public void setX(int x) {
		this.x = x;
	}
	
	public int getX() {
		return x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getY() {
		return y;
	}	
	
	public void setLayer(int layer) {
		this.layer = layer;
	}
	
	public int getLayer() {
		return layer;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public IThing getThing() {
		return thing;
	}

	@Override
	public void writeExternal(ObjectOutput out) throws IOException {
		out.writeInt(layer);
		out.writeInt(x);
		out.writeInt(y);
		out.writeBoolean(selected);
		out.writeObject(thing);
	}

	@Override
	public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
		layer = in.readInt();
		x = in.readInt();
		y = in.readInt();
		selected = in.readBoolean();
		thing = (IThing)in.readObject();
	}
}