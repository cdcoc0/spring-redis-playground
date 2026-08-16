package com.kirri.redis.service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class VerificationService {

	private static final Duration VERIFICATION_TTL = Duration.ofMinutes(3);
	private static final String VERIFICATION_KEY_PREFIX = "verification:";

	private final RedisService redisService;

	// 코드 생성
	public String createCode(String phone) {
		String code = generateCode();

		// 전화번호별 인증번호를 3분 동안만 유효하게 저장한다.
		redisService.setWithTtl(buildKey(phone), code, VERIFICATION_TTL);
		return code;
	}

	// 코드 검증
	public boolean verifyCode(String phone, String inputCode) {
		String key = buildKey(phone);
		String savedCode = redisService.get(key);

		if (savedCode == null) {
			return false;
		}

		if (!savedCode.equals(inputCode)) {
			return false;
		}

		// 인증번호는 1회성 값이므로 성공 시 바로 제거한다.
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
