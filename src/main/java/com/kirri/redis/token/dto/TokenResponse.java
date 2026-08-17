package com.kirri.redis.token.dto;

public record TokenResponse(
	Long userId,
	String accessToken,
	String refreshToken,
	long accessTokenTtlSeconds,
	long refreshTokenTtlSeconds
) {
}
