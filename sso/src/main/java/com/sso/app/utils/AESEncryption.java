package com.sso.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.spec.KeySpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.http.HttpStatus;

import com.constants.constants.Constants;
import com.sso.app.exception.CustomException;

public class AESEncryption {

	private static SecretKeySpec secretKeyspec;
	private static final String ALGORITHM = "AES";
	private static final String PADDING_ALGORITHM = "AES/ECB/PKCS5Padding";
	private static final String KEY = "AESEncryption";
	private static final String SALT = "AESsalt";
	private static final int ITERATION_COUNT = 100000;
	private static final int KEY_LENGTH = 256;

	static {
		try {
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
			KeySpec keySpec = new PBEKeySpec(KEY.toCharArray(), SALT.getBytes(), ITERATION_COUNT, KEY_LENGTH);
			SecretKey secretKey = keyFactory.generateSecret(keySpec);
			secretKeyspec = new SecretKeySpec(secretKey.getEncoded(), ALGORITHM); // Use AES as the algorithm here
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String encrypt(String plainText) {
		try {
			Cipher cipher = Cipher.getInstance(PADDING_ALGORITHM);
			cipher.init(Cipher.ENCRYPT_MODE, secretKeyspec);
			byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(Constants.DEFAULT_ERROR_MESSAGE, "ERR-5001", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	public static String decrypt(String encryptedText) {
		try {
			Cipher cipher = Cipher.getInstance(PADDING_ALGORITHM);
			cipher.init(Cipher.DECRYPT_MODE, secretKeyspec);
			byte[] decodedBytes = Base64.getDecoder().decode(encryptedText);
			byte[] decryptedBytes = cipher.doFinal(decodedBytes);
			return new String(decryptedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			throw new CustomException(Constants.DEFAULT_ERROR_MESSAGE, "ERR-5001", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}