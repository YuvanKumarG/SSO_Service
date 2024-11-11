package com.sso.app.model;

import java.sql.Timestamp;
import java.util.UUID;

import lombok.Data;

@Data
public class LoginResponse {
	
	private String accessToken;
	private String refreshToken;
	private UUID id;
	private String emailId;
	private String mobileNumber;
	private String source;
	private String firstName;
	private String lastName;
	private boolean mobileNumberVerified;
	private boolean emailIDVerified;
	private Timestamp lastLoginTime;
}
