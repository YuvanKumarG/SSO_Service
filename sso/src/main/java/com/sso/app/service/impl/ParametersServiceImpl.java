package com.sso.app.service.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.sso.app.entity.Parameters;
import com.sso.app.repo.ParametersRepo;
import com.sso.app.utils.CommonUtils;

@Service
public class ParametersServiceImpl {

	private final Logger logger = LoggerFactory.getLogger(ParametersServiceImpl.class);

	private static final String CACHE_NAME = "parameterTableCache";

	@Autowired
	private ParametersRepo parametersRepo;

	private final CacheManager cacheManager;

	@Autowired
	CommonUtils utils;

	public ParametersServiceImpl(CacheManager cacheManager) {
		this.cacheManager = cacheManager;
	}

	public String findByValue(String key) {
		Cache cache = cacheManager.getCache(CACHE_NAME);
		if (cache != null) {
			Cache.ValueWrapper cachedValue = cache.get(key);
			if (cachedValue != null) {
				logger.info("Retrieved value from cache for key: {}", key);
				return (String) cachedValue.get();
			}
		}
		
		CompletableFuture.runAsync(() -> {
			List<Parameters> parametersList = findAllParameters();
			Map<String, String> parametersMap = parametersList.stream()
					.collect(Collectors.toMap(Parameters::getKey, Parameters::getValue));

			if (cache != null) {
				parametersMap.forEach(cache::put);
				logger.info("Cached entire parameter table");
			}
		});
		
		Parameters param = parametersRepo.findFirstByKeyAndActive(key, true);
		return param.getValue();
	}

	@Cacheable(value = CACHE_NAME, key = "'parametersKey'")
	public List<Parameters> findAllParameters() {
		logger.info("Fetching all parameters from the database");
		return parametersRepo.findAll();
	}

	public void clearCache() {
		Cache cache = cacheManager.getCache("parameterTableCache");
		if (cache != null) {
			cache.clear();
			logger.info("Cache name: {} cleared at: {}", CACHE_NAME, utils.getCurrentTimestamp());
		}
	}
}
