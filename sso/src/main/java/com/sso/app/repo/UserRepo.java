package com.sso.app.repo;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sso.app.entity.User;


@Repository
public interface UserRepo extends JpaRepository<User, UUID> {

	long countByUsername(String username);
	
	long countByEmailId(String emailId);
	
	long countByMobileNumber(String mobileNumber);
	
	User findFirstById(UUID id);
	
	User findFirstByEmailId(String emailId);
}
