package com.kirri.redis.verification.service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import com.kirri.redis.basic.service.RedisService;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VerificationService {

	private static final Duration VERIFICATION_TTL = Duration.ofMinutes(3);
	private static final String VERIFICATION_KEY_PREFIX = "verification:";

	private final RedisService redisService;

	// Generates a random six-digit code and stores it with TTL.
	public String createCode(String phone) {
		String code = generateCode();

		// Verification codes are valid for only three minutes.
		redisService.setWithTtl(buildKey(phone), code, VERIFICATION_TTL);
		return code;
	}

	// Compares the user input against the stored verification code.
	public boolean verifyCode(String phone, String inputCode) {
		String key = buildKey(phone);
		String savedCode = redisService.get(key);

		if (savedCode == null) {
			return false;
		}

		if (!savedCode.equals(inputCode)) {
			return false;
		}

		// Codes are one-time values, so remove them after success.
		redisService.delete(key);
		return true;
	}

	public Long getRemainingSeconds(String phone) {
		return redisService.getExpire(buildKey(phone));
	}

	private String buildKey(String phone) {
		return VERIFICATION_KEY_PREFIX + phone;
	}

	private String generateCode() {
		int number = ThreadLocalRandom.current().nextInt(100000, 1_000_000);
		return String.valueOf(number);
	}
}
