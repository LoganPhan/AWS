package com.cognito.exceptions;

public class InvalidRequestException extends Exception {

	private static final long serialVersionUID = -4343210899494302582L;

	public InvalidRequestException(String message) {
        super(message);
    }
}
