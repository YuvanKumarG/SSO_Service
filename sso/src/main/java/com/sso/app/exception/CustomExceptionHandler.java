package com.sso.app.exception;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.constants.constants.Constants;
import com.sso.app.model.Response;

@RestControllerAdvice
//@SuppressWarnings({ "unchecked", "deprecation" })
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

	private Logger logger = LoggerFactory.getLogger(CustomExceptionHandler.class);

	@ExceptionHandler(CustomException.class)
	public ResponseEntity<Object> handleCustomExpectionHandler(CustomException e) {
		logger.error("Custom exception message is: {}", e.getMessage());
//		logger.error("Custom exception stack trace is: {}", ExceptionUtils.getStackTrace(e));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Response response = new Response();
		response.setErrorCode(e.getErrorCode());
		response.setErrorMessage(e.getMessage());
		return new ResponseEntity<>(response, headers, e.getHttpStatus());
	}

	@Override
	public final ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {

		logger.error("Validation error: {}", ex.getMessage());
		String errorMessage = ex.getBindingResult().getAllErrors().stream().map(error -> error.getDefaultMessage())
				.collect(Collectors.toSet()).stream().collect(Collectors.joining(","));
		headers.setContentType(MediaType.APPLICATION_JSON);
		Response response = new Response();
		response.setErrorCode("ERROR - 4001");
		response.setErrorMessage(errorMessage);
		return new ResponseEntity<>(response, headers, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<Object> exceptionHandler(Exception e) {
		logger.error("Exception message is: {}", e.getMessage());
//		logger.error("Exception stack trace is: {}", ExceptionUtils.getStackTrace(e));
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		Response response = new Response();
		response.setErrorCode("ERROR-5001");
		response.setErrorMessage(Constants.DEFAULT_ERROR_MESSAGE);
		return new ResponseEntity<>(response, headers, HttpStatus.INTERNAL_SERVER_ERROR);

	}

}
