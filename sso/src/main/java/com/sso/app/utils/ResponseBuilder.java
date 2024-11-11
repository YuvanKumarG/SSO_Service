package com.sso.app.utils;

import org.json.JSONObject;
import org.springframework.beans.BeanUtils;

import com.constants.constants.Constants;
import com.constants.constants.SSOEnum;
import com.sso.app.entity.User;
import com.sso.app.model.AccessTokenResponse;
import com.sso.app.model.LoginResponse;
import com.sso.app.model.RegistrationResponse;
import com.sso.app.model.Response;

public class ResponseBuilder {


	public static Response buildLoginResponse(User userData, String accessToken, String refreshToken) {
		Response response = new Response();
		response.setStatus(SSOEnum.ResponseStatus.SUCCESS);
		LoginResponse loginResponse = new LoginResponse();
		BeanUtils.copyProperties(userData, loginResponse);
		loginResponse.setAccessToken(accessToken);
		loginResponse.setRefreshToken(refreshToken);
		response.setResponse(loginResponse);
		return response;
	}

	public static Response buildRegistrationResponse() {
		Response response = new Response();
		response.setStatus(SSOEnum.ResponseStatus.SUCCESS);
		RegistrationResponse registrationResponse = new RegistrationResponse();
		registrationResponse.setMessage(Constants.USER_CREATED_SUCCESS_MESSAGE);
		response.setResponse(registrationResponse);
		return response;
	}

	public static Response buildAccessTokenResponse(JSONObject tokenDetails) {
		Response response = new Response();
		response.setStatus(SSOEnum.ResponseStatus.SUCCESS);
		AccessTokenResponse accessTokenResponse = new AccessTokenResponse();
		accessTokenResponse.setTokenStatus("ACTIVE");
		accessTokenResponse.setTokenDetails(tokenDetails.toMap());
		response.setResponse(accessTokenResponse);
		return response;
	}

}
