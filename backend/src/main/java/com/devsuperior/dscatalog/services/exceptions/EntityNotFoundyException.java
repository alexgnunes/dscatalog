package com.devsuperior.dscatalog.services.exceptions;

public class EntityNotFoundyException extends RuntimeException{
	private static final long serialVersionUID = 1L;

	public EntityNotFoundyException(String msg) {
		super(msg);
	}
}
