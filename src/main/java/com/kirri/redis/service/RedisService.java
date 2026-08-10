package com.kirri.redis.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	public RedisService(RedisTemplate<String, String> redisTemplate) {
		this.redisTemplate = redisTemplate;
	}

	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}
}
