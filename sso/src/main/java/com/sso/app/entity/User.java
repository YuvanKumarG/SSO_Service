package com.sso.app.entity;

import java.sql.Timestamp;
import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UpdateTimestamp;

import com.constants.constants.Constants;

import lombok.Data;

@Entity
@Table(name = "users")
@Data
public class User {

	@Id
	@GeneratedValue(generator = "UUID")
	@GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
	@Column(name = "id")
	private UUID id;

	@Column(name = "user_name", unique = true, nullable = false)
	private String username;

	@Column(name = "email_id", unique = true, nullable = false)
	private String emailId;

	@Column(name = "mobile_number", unique = true, nullable = false)
	private String mobileNumber;

	@Column(name = "password", nullable = false)
	private String password;

	@Column(name = "source", nullable = false)
	private String source;

	@CreationTimestamp
	@Column(name = "created_on", nullable = false)
	private Timestamp createdOn;

	@UpdateTimestamp
	@Column(name = "updated_on", nullable = false)
	private Timestamp updatedOn;

	@Column(name = "first_name", nullable = false)
	private String firstName;

	@Column(name = "last_name", nullable = false)
	private String lastName;

	@Column(name = "registration_status")
	private String registrationStatus = Constants.INITIATED;

	@Column(name = "is_mobile_number_verified", columnDefinition = "boolean default false")
	private boolean mobileNumberVerified;

	@Column(name = "is_email_id_verified", columnDefinition = "boolean default false")
	private boolean emailIDVerified;

	@Column(name = "last_login_time")
	private Timestamp lastLoginTime;
	
	@Column(name = "is_account_locked", columnDefinition = "boolean default false")
	private boolean accountLocked;

}
