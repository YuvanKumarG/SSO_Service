package com.sso.app.payload;

import com.constants.constants.RegexConstants;
import com.constants.constants.validation.CustomValidator;

import lombok.Data;

@Data
public class VerifyUserPayload {

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Email id is mandatory", regexPattern = RegexConstants.EMAIL_ID_REGEX, message = "Invalid email id")
	private String emailId;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Password is mandatory")
	private String password;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Checksum is mandatory")
	private String checksum;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Source is mandatory", acceptedValues = { "APP",
			"WEB" })
	private String source;
}
