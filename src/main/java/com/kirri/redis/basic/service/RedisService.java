package com.kirri.redis.basic.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	// Stores a basic key-value pair with no expiration.
	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	// Stores temporary data together with its TTL.
	public void setWithTtl(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl);
	}

	// Reads a string value from Redis.
	public String get(String key) {
		return redisTemplate.opsForValue().get(key);
	}

	public Long getExpire(String key) {
		return redisTemplate.getExpire(key);
	}

	public Boolean delete(String key) {
		return redisTemplate.delete(key);
	}
}
