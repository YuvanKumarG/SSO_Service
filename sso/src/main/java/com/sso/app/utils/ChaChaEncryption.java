package com.sso.app.utils;

import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.constants.constants.Constants;
import com.sso.app.exception.CustomException;

@Service
public class ChaChaEncryption {

	public static final String KEY = "CHACHA2020CHACHA2020CHACHA2020YU";

	static {
		Security.addProvider(new BouncyCastleProvider());
	}

	public static SecretKey generateKey() {
		try {
			// Ensure the key is exactly 32 bytes
			byte[] keyBytes = KEY.getBytes(StandardCharsets.UTF_8);
			if (keyBytes.length != 32) {
				System.out.println("Key length is " + keyBytes.length + " bytes. It must be 32 bytes for ChaCha20.");
				throw new IllegalArgumentException("Key must be 32 bytes for ChaCha20");
			}
			return new SecretKeySpec(keyBytes, "ChaCha20");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] generateNonce() {
		String value = "123456789123";
		return value.getBytes(StandardCharsets.UTF_8);
	}

	public static String encrypt(String plainText, SecretKey secretKey, byte[] nonce) {
		try {
			Cipher cipher = Cipher.getInstance("ChaCha20");
			IvParameterSpec ivParams = new IvParameterSpec(nonce);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParams);
			return Base64.getEncoder().encodeToString(cipher.doFinal(plainText.getBytes()));
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new CustomException(Constants.DEFAULT_ERROR_MESSAGE, "ERR-5001", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	public static String decrypt(String plainText, SecretKey secretKey, byte[] nonce) {
		try {
			Cipher cipher = Cipher.getInstance("ChaCha20");
			IvParameterSpec ivParams = new IvParameterSpec(nonce);
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParams);
			byte[] encryptedBytes = Base64.getDecoder().decode(plainText);
			byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
			return new String(decryptedBytes, StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
		}
		throw new CustomException(Constants.DEFAULT_ERROR_MESSAGE, "ERR-5001", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}
