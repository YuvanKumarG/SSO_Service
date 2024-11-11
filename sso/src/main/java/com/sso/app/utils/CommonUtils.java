package com.sso.app.utils;

import java.sql.Timestamp;
import java.time.Instant;

import org.springframework.stereotype.Service;

@Service
public class CommonUtils {

	public Timestamp getCurrentTimestamp() {
		return Timestamp.from(Instant.now());
	}

}
