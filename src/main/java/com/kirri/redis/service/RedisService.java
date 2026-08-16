package com.kirri.redis.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class RedisService {

	private final RedisTemplate<String, String> redisTemplate;

	// redis에 key, value 저장
	public void set(String key, String value) {
		redisTemplate.opsForValue().set(key, value);
	}

	// TTL이 필요한 값은 만료 시간과 함께 저장
	public void setWithTtl(String key, String value, Duration ttl) {
		redisTemplate.opsForValue().set(key, value, ttl);
	}

	// redis 조회
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
