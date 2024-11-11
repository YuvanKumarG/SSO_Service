package com.sso.app.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.constants.constants.Constants;
import com.constants.constants.SSOEnum;
import com.sso.app.entity.UserToken;
import com.sso.app.repo.UserTokenRepo;
import com.sso.app.utils.CommonUtils;

@Service
public class SchedulerServiceImpl {

	private Logger logger = LoggerFactory.getLogger(SchedulerServiceImpl.class);

	@Autowired
	ParametersServiceImpl parametersServiceImpl;

	@Autowired
	CommonUtils utils;

	@Autowired
	UserTokenRepo userTokenRepo;

	@Scheduled(cron = "${parameter.cache.clear.timing}")
	public void clearParameterCache() {
		parametersServiceImpl.clearCache();
	}

	@Scheduled(cron = "${session.inactive.scheduler}")
	public void inactiveSession() {
		logger.info("Inactive the session started: {}", utils.getCurrentTimestamp());
		int limit = Integer.parseInt(parametersServiceImpl.findByValue(Constants.MAXIMUM_INACTIVE_RECORD_COUNT));
		while (true) {
			Pageable pageable = PageRequest.of(0, limit, Sort.by("id").ascending());
			List<UserToken> tokenList = userTokenRepo.findByActiveAndTokenTypeAndExpiresAtLessThanEqual(true,
					SSOEnum.TokenType.ACCESS_TOKEN.toString(), utils.getCurrentTimestamp(), pageable);
			logger.info("Record count is: {}", tokenList.size());
			if (tokenList.isEmpty()) {
				logger.info("No records found, Stopping the loop");
				break;
			}
			tokenList.forEach(token -> updateTokenRemarks(token));
			userTokenRepo.saveAll(tokenList);
		}
		logger.info("Inactive the session ended: {}", utils.getCurrentTimestamp());
	}
	
	private void updateTokenRemarks(UserToken token) {
		token.setActive(false);
		token.setRemarks("Session inactived through the scheduler");
	}

}
