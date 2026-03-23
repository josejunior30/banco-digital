package com.junior.conta_transf.exception;

public class ClienteNaoEncontradoException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public ClienteNaoEncontradoException(String message) {
        super(message);
    }
}