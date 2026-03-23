package com.junior.conta_transf.exception;

public class ExternalServiceUnavailableException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ExternalServiceUnavailableException(String message) {
        super(message);
    }

    public ExternalServiceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}