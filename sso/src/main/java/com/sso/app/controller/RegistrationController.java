package com.sso.app.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.GsonBuilder;
import com.sso.app.payload.CreateUserPayload;
import com.sso.app.payload.VerifyUserPayload;
import com.sso.app.service.RegistrationService;
import com.sso.app.utils.PasswordEncryption;

@RestController
@CrossOrigin(allowedHeaders = "*")
public class RegistrationController {

	private Logger logger = LoggerFactory.getLogger(RegistrationController.class);

	@Autowired
	RegistrationService registrationService;

	@Autowired
	PasswordEncryption pass;

	@GetMapping(path = "/get", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> get() {
		logger.info("Request received");
		return new ResponseEntity<>("Success", HttpStatus.OK);
	}

	@PostMapping(path = "/create/user", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> createuser(@Valid @RequestBody CreateUserPayload request) {
		logger.info("Request to create user: {}", new GsonBuilder().serializeNulls().create().toJson(request));
		Object response = registrationService.signUpUser(request);
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@GetMapping(path = "/encrypt")
	public String encrypt(@RequestParam(name = "value") String value) {
		return pass.encrypt(value);
	}

	@PostMapping(path = "/decrypt")
	public String decrypt(@RequestBody String value) {
		return pass.decrypt(value);
	}

	@GetMapping(path = "/verify/emailId", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> verifyEmailID(@RequestHeader(name = "authorization") String token) {
		registrationService.verifyEmailID(token);
		return ResponseEntity.noContent().build();
	}

	@PostMapping(path = "/verfiy/user", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
			MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> verifyUser(@Valid @RequestBody VerifyUserPayload requestPayload) {
		logger.info("Request to verify user: {}", new GsonBuilder().serializeNulls().create().toJson(requestPayload));
		Object response = registrationService.verifyUser(requestPayload);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@GetMapping(path = "/verfiy/access/token", produces = { MediaType.APPLICATION_JSON_VALUE })
	public ResponseEntity<Object> verifyAccessToken(@RequestHeader(name = "authorization") String token,
			@RequestHeader(name="userId") String userID) {
		Object response = registrationService.verifyAccessToken(token,userID);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	@GetMapping(path = "/inactive/session",produces = {MediaType.APPLICATION_JSON_VALUE})
	public ResponseEntity<Object> inactiveSession(@RequestHeader(name = "authorization") String token,
			@RequestHeader(name="userId") String userID){
		registrationService.inactiveSession(token, userID);
		return ResponseEntity.noContent().build();
	}

}
