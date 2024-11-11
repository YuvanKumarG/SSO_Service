package com.sso.app.payload;

import com.constants.constants.RegexConstants;
import com.constants.constants.validation.CustomValidator;

import lombok.Data;

@Data
public class CreateUserPayload {

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Username is mandatory")
	private String username;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Mobile numner is mandatory", regexPattern = RegexConstants.MOBILE_NUMBER_REGEX, message = "Invalid mobile number", minFieldLength = 10, maxFieldLength = 10)
	private String mobileNumber;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Email id mandatory", regexPattern = RegexConstants.EMAIL_ID_REGEX, message = "Invalid email id")
	private String emailId;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Password is mandatory")
	private String password;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "First name is mandatory", regexPattern = RegexConstants.NAME_REGEX, message = "Invalid first name")
	private String firstName;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Last name is mandatory", regexPattern = RegexConstants.NAME_REGEX, message = "Invalid last name")
	private String lastName;

	@CustomValidator(isMandatoryField = true)
	private String checksum;

	@CustomValidator(isMandatoryField = true, mandatoryErrorMessage = "Source is mandatory", acceptedValues = {"WEB",
			"APP"}, message = "Invalid request")
	private String source;
}
