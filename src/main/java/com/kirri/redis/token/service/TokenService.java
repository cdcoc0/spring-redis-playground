package com.kirri.redis.token.service;

import java.time.Duration;
import java.util.UUID;

import com.kirri.redis.basic.service.RedisService;
import com.kirri.redis.token.dto.TokenResponse;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenService {

	private static final Duration ACCESS_TOKEN_TTL = Duration.ofMinutes(30);
	private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(1);
	private static final String REFRESH_KEY_PREFIX = "refresh:";
	private static final String ACCESS_KEY_PREFIX = "access:";
	private static final String BLACKLIST_KEY_PREFIX = "blacklist:";

	private final RedisService redisService;

	public TokenResponse login(Long userId) {
		String accessToken = generateToken();
		String refreshToken = generateToken();

		storeRefreshToken(userId, refreshToken);
		storeAccessToken(userId, accessToken);

		return new TokenResponse(
			userId,
			accessToken,
			refreshToken,
			ACCESS_TOKEN_TTL.getSeconds(),
			REFRESH_TOKEN_TTL.getSeconds()
		);
	}

	public TokenResponse refresh(Long userId, String refreshToken) {
		String savedRefreshToken = redisService.get(refreshKey(userId));

		if (savedRefreshToken == null) {
			throw new IllegalArgumentException("Refresh token expired or not found");
		}

		if (!savedRefreshToken.equals(refreshToken)) {
			throw new IllegalArgumentException("Invalid refresh token");
		}

		String newAccessToken = generateToken();
		String newRefreshToken = generateToken();

		storeRefreshToken(userId, newRefreshToken);
		storeAccessToken(userId, newAccessToken);

		return new TokenResponse(
			userId,
			newAccessToken,
			newRefreshToken,
			ACCESS_TOKEN_TTL.getSeconds(),
			REFRESH_TOKEN_TTL.getSeconds()
		);
	}

	public void logout(Long userId, String accessToken) {
		Long remainingSeconds = redisService.getExpire(accessKey(accessToken));

		if (remainingSeconds == null || remainingSeconds <= 0) {
			throw new IllegalArgumentException("Access token key does not exist");
		}

		// A logged-out access token is kept only for its remaining lifetime.
		redisService.setWithTtl(
			blacklistKey(accessToken),
			"logout",
			Duration.ofSeconds(remainingSeconds)
		);

		redisService.delete(accessKey(accessToken));
		redisService.delete(refreshKey(userId));
	}

	public boolean isBlacklisted(String accessToken) {
		return redisService.get(blacklistKey(accessToken)) != null;
	}

	private void storeRefreshToken(Long userId, String refreshToken) {
		redisService.setWithTtl(refreshKey(userId), refreshToken, REFRESH_TOKEN_TTL);
	}

	private void storeAccessToken(Long userId, String accessToken) {
		// Storing access tokens makes it easy to measure remaining TTL at logout.
		redisService.setWithTtl(accessKey(accessToken), String.valueOf(userId), ACCESS_TOKEN_TTL);
	}

	private String refreshKey(Long userId) {
		return REFRESH_KEY_PREFIX + userId;
	}

	private String accessKey(String accessToken) {
		return ACCESS_KEY_PREFIX + accessToken;
	}

	private String blacklistKey(String accessToken) {
		return BLACKLIST_KEY_PREFIX + accessToken;
	}

	private String generateToken() {
		return UUID.randomUUID().toString();
	}
}
