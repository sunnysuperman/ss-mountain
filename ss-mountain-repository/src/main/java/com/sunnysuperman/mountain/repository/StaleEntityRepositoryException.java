package com.sunnysuperman.mountain.repository;

@SuppressWarnings("serial")
public class StaleEntityRepositoryException extends RepositoryException {

	public StaleEntityRepositoryException() {
	}

	public StaleEntityRepositoryException(String msg) {
		super(msg);
	}

}
