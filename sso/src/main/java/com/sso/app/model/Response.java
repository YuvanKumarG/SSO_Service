package com.sso.app.model;

import org.apache.commons.lang3.StringUtils;

import com.constants.constants.SSOEnum;
import com.constants.constants.SSOEnum.ResponseStatus;

import lombok.Data;

@Data
public class Response {
	
	private ResponseStatus status=SSOEnum.ResponseStatus.FAILURE;
	private String errorMessage=StringUtils.EMPTY;
	private String errorCode="ERROR-0";
	private Object response;
	
}
