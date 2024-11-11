package com.sso.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.constants.constants.Constants;
import com.constants.constants.SSOEnum;
import com.google.gson.GsonBuilder;
import com.sso.app.entity.User;
import com.sso.app.entity.UserToken;
import com.sso.app.exception.CustomException;
import com.sso.app.repo.UserTokenRepo;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

@Service
public class JWTUtil {

	private Logger logger = LoggerFactory.getLogger(JWTUtil.class);

	private String secretKeyValueSSO = "SSO_SYSTEMSSO_SYSTEMSSO_SYSTEMSSO_SYSTEMSSO_SYSTEM";
	private String emailVerificationsecretKeyValue = "EMAIL_VERFICATION_SECRET_EMAIL_VERFICATION_SECRET_EMAIL_VERFICATION_SECRET";
	private Key secretKeySSO = Keys.hmacShaKeyFor(secretKeyValueSSO.getBytes(StandardCharsets.UTF_8));
	private Key emailVerificationSecretKey = Keys
			.hmacShaKeyFor(emailVerificationsecretKeyValue.getBytes(StandardCharsets.UTF_8));

	@Autowired
	UserTokenRepo userTokenRepo;

	@Autowired
	CommonUtils utils;

	@Autowired
	RedisUtil redisutil;

	private String generateToken(JSONObject tokenDetailsJson, Map<String, Object> header, Key secretKey,
			Date expireTime) {
		return Jwts.builder().setId(AESEncryption.encrypt(tokenDetailsJson.toString())).setExpiration(expireTime)
				.setIssuedAt(new Date(System.currentTimeMillis())).setIssuer(Constants.SSO).setHeader(header)
				.signWith(secretKey, SignatureAlgorithm.HS384).compact();
	}

	private Date getExpireTime(SSOEnum.JwtExpiryFrequency frequency, long expiryValue) {
		Date expiryDate = null;
		switch (frequency) {
		case MINUTES:
			expiryDate = new Date(System.currentTimeMillis() + 60000 * expiryValue);
			break;
		case HOURS:
			expiryDate = new Date(System.currentTimeMillis() + 3600000 * expiryValue);
			break;
		case DAYS:
			expiryDate = new Date(System.currentTimeMillis() + 86400000 * expiryValue);
			break;
		default:
			expiryDate = new Date(System.currentTimeMillis());
			break;

		}
		return expiryDate;
	}

	public JSONObject verifyToken(String token, Key secretKey) {
		try {
			token = token.replaceAll("Bearer ", StringUtils.EMPTY);
			Claims tokenDetails = Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
			if (StringUtils.isEmpty(tokenDetails.getId())) {
				logger.error("Details inside the token is empty or null");
				throw new CustomException("UNAUTHORIZED", "ERROR-401", HttpStatus.UNAUTHORIZED);
			}
			JSONObject jsonObject = new JSONObject(AESEncryption.decrypt(tokenDetails.getId()));
			return jsonObject;
		} catch (Exception e) {
			logger.error("Excpetion occured in verify token: {}", e.getMessage());
			logger.error("Exception stack trace is: {}", ExceptionUtils.getStackTrace(e));
			throw new CustomException("UNAUTHORIZED", "ERROR-401", HttpStatus.UNAUTHORIZED);
		}
	}

	/*
	 * Generate the email verification link
	 */
	public String generateEmailVerificationLink(User userData) {
		Map<String, Object> header = new HashMap<>();
		header.put("type", "emailverification_token");
		JSONObject tokenDetailsJson = new JSONObject();
		tokenDetailsJson.put("userId", userData.getId());
		tokenDetailsJson.put("emailId", userData.getEmailId());
		return generateToken(tokenDetailsJson, header, emailVerificationSecretKey,
				getExpireTime(SSOEnum.JwtExpiryFrequency.MINUTES, 15));
	}

	/*
	 * Verification of the user
	 */
	public JSONObject verifyEmailVerificationToken(String token) {
		logger.info("Request to verify email verfication token: {}", token);
		if (StringUtils.isEmpty(token)) {
			logger.error("Token is null or empty");
			throw new CustomException("UNAUTHORIZED", "ERROR-401", HttpStatus.UNAUTHORIZED);
		}
		return verifyToken(token, emailVerificationSecretKey);
	}

	public String generateLoginAccessToken(User userData, UserToken userToken) {
		Map<String, Object> loginHeader = new HashMap<>();
		loginHeader.put("type", SSOEnum.TokenType.ACCESS_TOKEN);
		JSONObject tokenDetails = new JSONObject();
		tokenDetails.put("emailId", userData.getEmailId());
		tokenDetails.put("userId", userData.getId());
		tokenDetails.put("mobileNumber", userData.getMobileNumber());
		tokenDetails.put("tokenType", SSOEnum.TokenType.ACCESS_TOKEN);
		Date expiryTime = getExpireTime(SSOEnum.JwtExpiryFrequency.DAYS, 7);
		String token = generateToken(tokenDetails, loginHeader, secretKeySSO, expiryTime);
		userToken.setActive(true);
		userToken.setTokenValue(token);
		userToken.setTokenType(SSOEnum.TokenType.ACCESS_TOKEN.toString());
		userToken.setIssuedAt(utils.getCurrentTimestamp());
		userToken.setExpiresAt(new Timestamp(expiryTime.getTime()));
		userToken.setUserId(userData.getId());
		logger.info("User token details: {}", new GsonBuilder().serializeNulls().create().toJson(userToken));
		userTokenRepo.save(userToken);
//		redisutil.
		return token;
	}

	public String generateLoginRefreshToken(User userData, UserToken userToken) {
		Map<String, Object> loginHeader = new HashMap<>();
		loginHeader.put("type", SSOEnum.TokenType.REFRESH_TOKEN);
		JSONObject tokenDetails = new JSONObject();
		tokenDetails.put("emailID", userData.getEmailId());
		tokenDetails.put("userID", userData.getId());
		tokenDetails.put("mobileNumber", userData.getMobileNumber());
		tokenDetails.put("tokenType", SSOEnum.TokenType.REFRESH_TOKEN);
		Date expiryTime = getExpireTime(SSOEnum.JwtExpiryFrequency.DAYS, 30);
		String token = generateToken(tokenDetails, loginHeader, secretKeySSO, expiryTime);
		userToken.setActive(true);
		userToken.setTokenValue(token);
		userToken.setTokenType(SSOEnum.TokenType.REFRESH_TOKEN.toString());
		userToken.setIssuedAt(utils.getCurrentTimestamp());
		userToken.setExpiresAt(new Timestamp(expiryTime.getTime()));
		userToken.setUserId(userData.getId());
		logger.info("User token details: {}", new GsonBuilder().serializeNulls().create().toJson(userToken));
		userTokenRepo.save(userToken);
		return token;
	}

	public JSONObject verifyAccessToken(String token) {
		return verifyToken(token, secretKeySSO);
	}

}
