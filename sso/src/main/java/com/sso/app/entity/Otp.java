package com.sso.app.entity;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;

@Entity
@Table(name = "otp")
@Data
public class Otp {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private long id;

	@Column(name = "otp_value")
	private String otpValue;

	@Column(name = "email_id")
	private String emailID;

	@Column(name = "mobile_number")
	private String mobileNumber;

	@CreationTimestamp
	@Column(name = "created_at")
	private Timestamp createdAt;

	@Column(name = "expires_at")
	private Timestamp expiresAt;

	@Column(name = "verified_at")
	private Timestamp verifiedAt;

	@Column(name = "user_id")
	private UUID uuid;
	
	@Column(name="is_verified")
	private boolean verified;
	
	@Column(name = "retry_attempts")
	private int retryAttempts;
}
