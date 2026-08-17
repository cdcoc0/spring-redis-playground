package com.kirri.redis.token.dto;

public record LogoutRequest(Long userId, String accessToken) {
}
