package com.sso.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.sso.app.exception.CustomException;

@Service
public class PasswordEncryption {
	private Logger logger = LoggerFactory.getLogger(PasswordEncryption.class);
	public static final String KEY = "jv@jfh%f*kh$=fdh^gfhvbhdfbhfd56df5vr43242#";

	private static PublicKey publicKey;
	private static PrivateKey privateKey;

	public PasswordEncryption() {
		generateKeyPair();
	}

	public void generateKeyPair() {
		try {
			KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
			SecureRandom secureRandom = new SecureRandom();
			byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
			secureRandom.setSeed(keyBytes);
			keyGen.initialize(2048, secureRandom);
			KeyPair keyPair = keyGen.generateKeyPair();
			PasswordEncryption.publicKey = keyPair.getPublic();
			PasswordEncryption.privateKey = keyPair.getPrivate();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String encrypt(String plainText) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String decrypt(String encryptedText) {
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
			return new String(decryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String decryptPassowrd(String password) {
		String plainText = decrypt(password);
		if (StringUtils.isBlank(plainText)) {
			logger.error("Unable to get the password as plain text");
			throw new CustomException("Invalid request.", "ERROR-4009", HttpStatus.BAD_REQUEST);
		}
		return plainText;
	}

}
