package com.sso.app.repo;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sso.app.entity.UserToken;
import java.util.UUID;
import java.sql.Timestamp;
import java.util.List;

@Repository
public interface UserTokenRepo extends JpaRepository<UserToken, Long> {

	long countByUniqueTokenID(UUID uniqueTokenID);

	List<UserToken> findByTokenValueAndTokenTypeAndActiveAndUserIdAndExpiresAtGreaterThanEqual(String tokenValue,
			String tokenType, boolean active, UUID userId, Timestamp currentTimestamp);

	long countByUserIdAndTokenTypeAndActiveAndExpiresAtGreaterThanEqual(UUID userId, String tokenType, boolean active,
			Timestamp currentTimestamp);

	List<UserToken> findByActiveAndTokenTypeAndExpiresAtLessThanEqual(boolean active, String tokenType,
			Timestamp expiresAt, Pageable pageable);
}
