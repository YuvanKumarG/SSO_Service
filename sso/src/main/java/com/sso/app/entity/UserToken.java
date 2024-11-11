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
import org.hibernate.annotations.UpdateTimestamp;

import lombok.Data;

@Entity
@Table(name = "user_tokens")
@Data
public class UserToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false, columnDefinition = "UUID")
	private UUID userId;

	@Column(name = "token_value", nullable = false, unique = true)
	private String tokenValue;

	@CreationTimestamp
	@Column(name = "issued_at", nullable = false)
	private Timestamp issuedAt;

	@Column(name = "expires_at", nullable = false)
	private Timestamp expiresAt;

	@Column(name = "is_active", nullable = false)
	private boolean active;

	@Column(name = "token_type", nullable = false)
	private String tokenType;

	@Column(name = "unique_token_id", nullable = false)
	private UUID uniqueTokenID;

	@CreationTimestamp
	@Column(name = "created_on", nullable = false)
	private Timestamp createdOn;

	@UpdateTimestamp
	@Column(name = "updated_on", nullable = false)
	private Timestamp updatedOn;

	@Column(name = "remarks")
	private String remarks;

}
