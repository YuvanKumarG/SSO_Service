package com.sso.app.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.constants.constants.Constants;
import com.sso.app.exception.CustomException;

@Service
public class HmacHash {

	private static final String HMAC_SECRET_KEY = "0cNsjdcHGY6^6%%$%84455__++NBKJSBC^TR%SCJGSCGSFbhhDVCGDbgbhhyVCG";
	private static final String HMAC_ALGORITHM = "HmacSHA256";

	private Logger logger = LoggerFactory.getLogger(HmacHash.class);

	public String generateHMAC(String data) {
		try {
			Mac mac = Mac.getInstance(HMAC_ALGORITHM);
			SecretKeySpec secretKeySpec = new SecretKeySpec(HMAC_SECRET_KEY.getBytes(), HMAC_ALGORITHM);
			mac.init(secretKeySpec);
			return Hex.encodeHexString(mac.doFinal(data.getBytes()));
		} catch (Exception e) {
			logger.error("Exception message: {} and stacktrace: {}", e.getMessage(), ExceptionUtils.getStackTrace(e));
			throw new CustomException(Constants.DEFAULT_ERROR_MESSAGE, "ERROR-4016", HttpStatus.INTERNAL_SERVER_ERROR);

		}
	}
}
