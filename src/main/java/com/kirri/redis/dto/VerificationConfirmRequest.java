package com.kirri.redis.dto;

public record VerificationConfirmRequest(String phone, String code) {
}
