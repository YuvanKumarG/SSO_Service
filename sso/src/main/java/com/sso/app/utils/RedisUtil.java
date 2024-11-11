package com.sso.app.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisUtil {
	
	private Logger logger= LoggerFactory.getLogger(RedisUtil.class);

	@Autowired
	StringRedisTemplate redisTemplate;

	public void pushToRedis(String key, String value) {
//		redisTemplate.
		logger.info("Successfully pushed the value to redis");
	}

}
