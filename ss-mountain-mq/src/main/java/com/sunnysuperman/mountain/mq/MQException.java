package com.sunnysuperman.mountain.mq;

@SuppressWarnings("serial")
public class MQException extends RuntimeException {

	public MQException() {
		super();
	}

	public MQException(String message) {
		super(message);
	}

	public MQException(Throwable cause) {
		super(cause);
	}

	public MQException(String message, Throwable cause) {
		super(message, cause);
	}

	public static MQException wrap(Throwable e) {
		if (e instanceof MQException) {
			return (MQException) e;
		}
		return new MQException(e);
	}

}
