package com.sunnysuperman.mountain.lang.exception.service;

public class ServiceExceptions {

	private ServiceExceptions() {
	}

	public static ServiceException wrap(Throwable ex) {
		if (ex instanceof ServiceException) {
			return (ServiceException) ex;
		}
		return new RuntimeServiceException(ex);
	}

	public static ServiceException extract(Throwable ex, int maxDepth) {
		int i = 0;
		while (ex != null) {
			if (ex instanceof ServiceException) {
				return (ServiceException) ex;
			}
			if (++i == maxDepth) {
				break;
			}
			ex = ex.getCause();
		}
		return null;
	}

	public static ServiceException extract(Throwable ex) {
		return extract(ex, -1);
	}
}
