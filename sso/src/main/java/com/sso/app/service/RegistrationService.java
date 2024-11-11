package com.sso.app.service;

import org.springframework.stereotype.Component;

import com.sso.app.payload.CreateUserPayload;
import com.sso.app.payload.VerifyUserPayload;

@Component
public interface RegistrationService {
	
	public Object signUpUser(CreateUserPayload userRequestPayload);
	
	public void verifyEmailID(String token);
	
	public Object verifyUser(VerifyUserPayload requestPayload);
	
	public Object verifyAccessToken(String token, String userID);
	
	public void inactiveSession(String token, String userID);

}
