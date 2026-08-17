package com.kirri.redis.token.dto;

public record RefreshTokenRequest(Long userId, String refreshToken) {
}
