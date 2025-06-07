package com.sunnysuperman.mountain.evt;

public enum EvtPriority {

	LOW,

	MEDIUM,

	HIGH,

	HIGHEST;

	public boolean higherOrEquals(EvtPriority p) {
		return ordinal() >= p.ordinal();
	}

}
