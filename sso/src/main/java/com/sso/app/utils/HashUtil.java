package com.sso.app.utils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.google.gson.GsonBuilder;
import com.sso.app.exception.CustomException;
import com.sso.app.payload.CreateUserPayload;
import com.sso.app.payload.VerifyUserPayload;

@Service
public class HashUtil {

	private Logger logger = LoggerFactory.getLogger(HashUtil.class);
	public static final String CREATE_USER_CHECKSUM_KEY = "021$#Oj5Y&%";
	public static final String VERIFY_USER_CHECKSUM_KEY = "87Gb$#(HF%$D+)9";

	public static String generateHMAC(String key, String value) {
		HmacUtils hmacUtils = new HmacUtils("HmacSHA256", key);
		byte[] hmacBytes = hmacUtils.hmac(value);
		return Hex.encodeHexString(hmacBytes);
	}

	public void verifyChecksumForCreateUser(CreateUserPayload requestPayload) {
		String checksumString = StringUtils.join(requestPayload.getFirstName(), requestPayload.getLastName(),
				requestPayload.getEmailId(), requestPayload.getMobileNumber(), requestPayload.getSource(),
				requestPayload.getPassword());
		String md5checksum = generateHMAC(CREATE_USER_CHECKSUM_KEY, checksumString);
		logger.info("System generated checksum: {}", md5checksum);

		if (!requestPayload.getChecksum().equals(md5checksum)) {
			logger.error("Both checksum are not matching");
			throw new CustomException("Invalid request.", "ERROR-4008", HttpStatus.BAD_REQUEST);
		}

		logger.info("Checksum verified successfully");
	}

	public void verifyChecksumForPassword(VerifyUserPayload requestPayload) {
		logger.info("Request to checkpassword checksum: {}",
				new GsonBuilder().serializeNulls().create().toJson(requestPayload));

		String checksumString = StringUtils.join(requestPayload.getEmailId(), requestPayload.getPassword(),
				requestPayload.getSource());
		logger.info("Checksum string is: {}", checksumString);
		String md5checksum = generateHMAC(VERIFY_USER_CHECKSUM_KEY, checksumString);
		logger.info("System generated checksum: {}", md5checksum);

		if (!requestPayload.getChecksum().equals(md5checksum)) {
			logger.error("Both checksum are not matching");
			throw new CustomException("Invalid request.", "ERROR-4007", HttpStatus.BAD_REQUEST);
		}

		logger.info("Checksum verified successfully");
	}

}
