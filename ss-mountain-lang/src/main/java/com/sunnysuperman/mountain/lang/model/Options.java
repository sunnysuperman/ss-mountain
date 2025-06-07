package com.sunnysuperman.mountain.lang.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

public abstract class Options {
	@JsonIgnore
	private boolean frozen;

	@SuppressWarnings("unchecked")
	public <T> T froze() {
		this.frozen = true;
		return (T) this;
	}

	public void checkFrozenStatus() {
		if (frozen) {
			throw new IllegalStateException("The object is frozen");
		}
	}

	public boolean isFrozen() {
		return frozen;
	}
}
