package com.sso.app.model;

import java.util.Map;

import lombok.Data;

@Data
public class AccessTokenResponse {
	private String tokenStatus;
	private Map<String, Object> tokenDetails;
}
