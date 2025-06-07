package com.sunnysuperman.mountain.evt;

@SuppressWarnings("serial")
public class EvtException extends RuntimeException {

	public EvtException() {
		super();
	}

	public EvtException(String message) {
		super(message);
	}

	public EvtException(Throwable cause) {
		super(cause);
	}

	public EvtException(String message, Throwable cause) {
		super(message, cause);
	}

	public EvtException(Evt evt, Throwable cause) {
		super("Consume event failed: " + evt.getClass().getCanonicalName() + "/" + evt.getId(), cause);
	}

}
