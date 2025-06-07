package com.sunnysuperman.mountain.repository;

public class SaveResult {
	public static final SaveResult RES_NONE = new SaveResult(false, false);
	public static final SaveResult RES_INSERTED = new SaveResult(true, false);
	public static final SaveResult RES_UPDATED = new SaveResult(false, true);

	private boolean inserted;
	private boolean updated;

	private SaveResult(boolean inserted, boolean updated) {
		super();
		this.inserted = inserted;
		this.updated = updated;
	}

	public boolean success() {
		return inserted || updated;
	}

	public boolean isInserted() {
		return inserted;
	}

	public boolean isUpdated() {
		return updated;
	}

}
