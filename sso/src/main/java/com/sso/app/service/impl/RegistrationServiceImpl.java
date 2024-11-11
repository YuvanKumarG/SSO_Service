package com.sso.app.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.constants.constants.Constants;
import com.constants.constants.RegexConstants;
import com.constants.constants.SSOEnum;
import com.sso.app.entity.User;
import com.sso.app.entity.UserToken;
import com.sso.app.exception.CustomException;
import com.sso.app.payload.CreateUserPayload;
import com.sso.app.payload.VerifyUserPayload;
import com.sso.app.repo.UserRepo;
import com.sso.app.repo.UserTokenRepo;
import com.sso.app.service.RegistrationService;
import com.sso.app.utils.CommonUtils;
import com.sso.app.utils.HashUtil;
import com.sso.app.utils.HmacHash;
import com.sso.app.utils.JWTUtil;
import com.sso.app.utils.PasswordEncryption;
import com.sso.app.utils.ResponseBuilder;

@Service
public class RegistrationServiceImpl implements RegistrationService {

	private Logger logger = LoggerFactory.getLogger(RegistrationServiceImpl.class);

	@Autowired
	UserRepo userRepo;

	@Autowired
	JWTUtil jwtUtil;

	@Autowired
	HashUtil hashUtil;

	@Autowired
	PasswordEncryption passwordEncryption;

	@Autowired
	CommonUtils utils;

	@Autowired
	UserTokenRepo userTokenRepo;

	@Autowired
	ParametersServiceImpl parametersServiceImpl;

	@Autowired
	HmacHash hmac;

	@Override
	public Object signUpUser(CreateUserPayload createUserPayload) {
		hashUtil.verifyChecksumForCreateUser(createUserPayload);
		checkConstraintsForCreateUser(createUserPayload);
		createUserPayload.setPassword(passwordEncryption.decryptPassowrd(createUserPayload.getPassword()));
		checkPasswordRegex(createUserPayload.getPassword());
		User user = new User();
		BeanUtils.copyProperties(createUserPayload, user);
		user.setUsername(user.getUsername().toUpperCase());
		user.setPassword(hmac.generateHMAC(user.getPassword()));
		final User userSavedInTable = userRepo.save(user);
		CompletableFuture.runAsync(() -> {
			try {
				sendEmailVerificationLink(userSavedInTable);
			} finally {

			}
		});

		return ResponseBuilder.buildRegistrationResponse();
	}

	private void checkPasswordRegex(String value) {
		Pattern pattern = Pattern.compile(RegexConstants.PASSWORD_REGEX);
		Matcher matcher = pattern.matcher(value);
		if (!matcher.matches()) {
			logger.error("Password does not meet required criteria.");
			throw new CustomException("Password does not meet required criteria.", "ERROR-4017",
					HttpStatus.BAD_REQUEST);
		}
	}

	@Override
	public void verifyEmailID(String token) {
		JSONObject tokenDetails = jwtUtil.verifyEmailVerificationToken(token);
		logger.info("Email details inside token: {}", tokenDetails.toString());
		if (!tokenDetails.has("userId") || StringUtils.isEmpty(tokenDetails.getString("userId"))) {
			throw new CustomException("UNAUTHORIZED", "ERROR-401", HttpStatus.UNAUTHORIZED);
		}
		UUID userId = UUID.fromString(tokenDetails.getString("userId"));
		User userDetails = userRepo.findFirstById(userId);
		if (null == userDetails) {
			logger.error("User details not found for this user id: {}", userId);
			throw new CustomException("User not found.", "ERROR-4005", HttpStatus.NOT_FOUND);
		}
		if (Constants.SUCCESS.equalsIgnoreCase(userDetails.getRegistrationStatus())) {
			logger.error("User is already verified: {}", userId);
			throw new CustomException("User is already verified.", "ERROR-4006", HttpStatus.OK);
		}
		userDetails.setRegistrationStatus(Constants.SUCCESS);
		userDetails.setEmailIDVerified(true);
		userRepo.save(userDetails);
		logger.info("User verified successfully: {}", userDetails.getId());

	}

	private void checkConstraintsForCreateUser(CreateUserPayload requestPayload) {
		checkUserNameIsUnique(requestPayload.getUsername());
		checkEmailIdUnique(requestPayload.getEmailId());
		checkMobileNoUnique(requestPayload.getMobileNumber());
	}

	private void checkMobileNoUnique(String mobileNumber) {
		long mobileNumberCount = userRepo.countByMobileNumber(mobileNumber);
		if (mobileNumberCount > 0) {
			logger.error("Mobile number: {} and count: {} is already present in table", mobileNumber,
					mobileNumberCount);
			throw new CustomException("User is already exists.", "ERROR-4004", HttpStatus.OK);

		}
	}

	private void checkEmailIdUnique(String emailId) {
		long emailIdCount = userRepo.countByEmailId(emailId);
		if (emailIdCount > 0) {
			logger.error("Email id: {} and count: {} is already present in table", emailId, emailIdCount);
			throw new CustomException("User is already exists.", "ERROR-4003", HttpStatus.OK);

		}
	}

	private void checkUserNameIsUnique(String username) {
		long userNameCount = userRepo.countByUsername(username.toUpperCase());
		if (userNameCount > 0) {
			logger.error("Username: {} and count: {} is already present in table", username, userNameCount);
			throw new CustomException("User is already exists.", "ERROR-4002", HttpStatus.OK);

		}
	}

	private void sendEmailVerificationLink(User userData) {
		logger.info("Request to send mail verfication for user id: {} and email id: {}", userData.getId(),
				userData.getEmailId());
		String token = jwtUtil.generateEmailVerificationLink(userData);
		logger.info("Email verfication token is: {}", token);
	}

	@Override
	public Object verifyUser(VerifyUserPayload requestPayload) {
		hashUtil.verifyChecksumForPassword(requestPayload);
		requestPayload.setPassword(passwordEncryption.decryptPassowrd(requestPayload.getPassword()));
		User userData = userRepo.findFirstByEmailId(requestPayload.getEmailId());
		if (null == userData) {
			logger.error("No data found");
			throw new CustomException("No account found with this information. Please sign up to create an account.",
					"ERROR-4012", HttpStatus.NOT_FOUND);
		}
		String password = hmac.generateHMAC(requestPayload.getPassword());
		logger.info("System generated password hash is: {}", password);
		if (!password.equalsIgnoreCase(userData.getPassword())) {
			logger.error("Password is not matching for the user: {} and email id: {}", userData.getId(),
					userData.getEmailId());
			throw new CustomException("Incorrect password", "ERROR-4010", HttpStatus.UNAUTHORIZED);
		}
		if (userData.isAccountLocked()) {
			logger.error("Account is locked for this user id: {} and email id: {}", userData.getId(),
					userData.getEmailId());
			throw new CustomException(Constants.DEFAULT_ERROR_MESSAGE, "ERROR-4013", HttpStatus.LOCKED);
		}
		if (Constants.INITIATED.equalsIgnoreCase(userData.getRegistrationStatus())) {
			logger.error("Email id is not verified for the user: {} and email id: {}", userData.getId(),
					userData.getEmailId());
			CompletableFuture.runAsync(() -> {
				sendEmailVerificationLink(userData);
			});
			throw new CustomException(
					"Email verification is required. Kindly check your inbox and follow the instructions to verify your email.",
					"ERROR-4011", HttpStatus.OK);
		}
		checkActiveSessionCount(userData.getId());
		userData.setLastLoginTime(utils.getCurrentTimestamp());
		userRepo.save(userData);
		UUID uniqueID = getUniqueUUIDForUserTokenTable();

		UserToken userAccessToken = new UserToken();
		userAccessToken.setUniqueTokenID(uniqueID);
		CompletableFuture<String> accessTokenFuture = CompletableFuture.supplyAsync(() -> {
			return jwtUtil.generateLoginAccessToken(userData, userAccessToken);
		});

		UserToken userRefreshToken = new UserToken();
		userRefreshToken.setUniqueTokenID(uniqueID);

		return accessTokenFuture
				.thenApply(accessToken -> ResponseBuilder.buildLoginResponse(userData, accessToken, StringUtils.EMPTY))
				.join();
	}

	private UUID getUniqueUUIDForUserTokenTable() {
		UUID uniqueID = UUID.randomUUID();
		long count = userTokenRepo.countByUniqueTokenID(uniqueID);
		while (0 > count) {
			uniqueID = UUID.randomUUID();
			count = userTokenRepo.countByUniqueTokenID(uniqueID);
		}
		return uniqueID;
	}

	private void checkActiveSessionCount(UUID userID) {
		long activeSessionCountForUser = userTokenRepo.countByUserIdAndTokenTypeAndActiveAndExpiresAtGreaterThanEqual(
				userID, SSOEnum.TokenType.ACCESS_TOKEN.toString(), true, utils.getCurrentTimestamp());
		long maxactiveSessionCount = Long
				.parseLong(parametersServiceImpl.findByValue(Constants.MAXIMUM_ACTIVE_SESSIONS));
		logger.info("Max session count: {} and active session for user is: {}", maxactiveSessionCount,
				activeSessionCountForUser);
		if (activeSessionCountForUser >= maxactiveSessionCount) {
			logger.error("Maximum session count reached for user id: {}", userID);
			throw new CustomException("Maximum session limit reached. Please log out from another device to continue.",
					"ERROR-4015", HttpStatus.FORBIDDEN);
		}
	}

	@Override
	public Object verifyAccessToken(String token, String userID) {
		logger.info("Request to verify the token: {} and user id: {}", token, userID);
		if (StringUtils.isAnyEmpty(token, userID)) {
			logger.error("User id or token is missing");
			throw new CustomException("Invalid request", "ERROR-4014", HttpStatus.BAD_REQUEST);
		}
		token = token.replaceAll("Bearer ", StringUtils.EMPTY);
		List<UserToken> tokenList = userTokenRepo
				.findByTokenValueAndTokenTypeAndActiveAndUserIdAndExpiresAtGreaterThanEqual(token,
						SSOEnum.TokenType.ACCESS_TOKEN.toString(), true, UUID.fromString(userID),
						utils.getCurrentTimestamp());
		if (tokenList.isEmpty()) {
			logger.error("Token is not present in the table");
			throw new CustomException("Access denied. Please contact the administrator", "ERROR-401",
					HttpStatus.UNAUTHORIZED);
		}
		JSONObject tokenJSON = jwtUtil.verifyAccessToken(token);
		logger.info("Details inside the token is: {}", tokenJSON.toString());
		return ResponseBuilder.buildAccessTokenResponse(tokenJSON);
	}

	@Override
	public void inactiveSession(String token, String userID) {
		logger.info("Request to inactive the session token: {} and user id: {}", token, userID);
		logger.info("Request to verify the token: {} and user id: {}", token, userID);
		if (StringUtils.isAnyEmpty(token, userID)) {
			logger.error("User id or token is missing");
			throw new CustomException("Invalid request", "ERROR-4014", HttpStatus.BAD_REQUEST);
		}
		token = token.replaceAll("Bearer ", StringUtils.EMPTY);
		List<UserToken> tokenList = userTokenRepo
				.findByTokenValueAndTokenTypeAndActiveAndUserIdAndExpiresAtGreaterThanEqual(token,
						SSOEnum.TokenType.ACCESS_TOKEN.toString(), true, UUID.fromString(userID),
						utils.getCurrentTimestamp());
		tokenList.stream().forEach(tokens -> tokens.setActive(false));
		userTokenRepo.saveAll(tokenList);
	}

}
