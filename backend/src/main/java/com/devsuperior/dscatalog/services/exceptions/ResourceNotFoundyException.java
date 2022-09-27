package com.devsuperior.dscatalog.services.exceptions;

public class ResourceNotFoundyException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public ResourceNotFoundyException(String msg) {
		super(msg);
	}
}
