package com.sunnysuperman.mountain.lang.exception.service;

public class NeedUpgradeServiceException extends ServiceException {
	private static final long serialVersionUID = 4080061760420572790L;

	public NeedUpgradeServiceException() {
		super(GenericServiceError.NEED_TO_UPGRADE);
	}
}
