package io.downgoon.autorest4db.mode;

public class AutoRestException extends RuntimeException {

	private static final long serialVersionUID = 5006663231518751109L;

	public AutoRestException() {
		super();
	}

	public AutoRestException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public AutoRestException(String message, Throwable cause) {
		super(message, cause);
	}

	public AutoRestException(String message) {
		super(message);
	}

	public AutoRestException(Throwable cause) {
		super(cause);
	}

	
}
