package com.sso.app.entity;

import java.sql.Timestamp;

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
@Table(name = "parameters")
@Data
public class Parameters {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id")
	private Long id;
	
	@Column(name = "key",nullable = false)
	private String key;
	
	@Column(name = "value",nullable = false)
	private String value;
	
	@Column(name = "is_active",columnDefinition = "boolean DEFAULT false")
	private boolean active;
	
	@CreationTimestamp
	@Column(name = "created_on", nullable = false)
	private Timestamp createdOn;

	@UpdateTimestamp
	@Column(name = "updated_on", nullable = false)
	private Timestamp updatedOn;

}
