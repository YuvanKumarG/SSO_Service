package com.sso.app.exception;

import org.springframework.http.HttpStatus;

public class CustomException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;
	private String errorCode;
	private HttpStatus httpStatus;

	public CustomException(String message, String errorCode, HttpStatus httpStatus) {
		super(message);
		this.errorCode = errorCode;
		this.httpStatus = httpStatus;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}
}
