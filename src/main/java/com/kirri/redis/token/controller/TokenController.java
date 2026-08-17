package com.kirri.redis.token.controller;

import java.util.Map;

import com.kirri.redis.token.dto.LoginRequest;
import com.kirri.redis.token.dto.LogoutRequest;
import com.kirri.redis.token.dto.RefreshTokenRequest;
import com.kirri.redis.token.dto.TokenResponse;
import com.kirri.redis.token.service.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@Tag(name = "Token Store", description = "UUID based token storage practice API")
@RestController
@RequestMapping("/tokens")
@RequiredArgsConstructor
public class TokenController {

	private final TokenService tokenService;

	@Operation(summary = "로그인", description = "UUID access token과 refresh token을 발급하고 Redis에 저장합니다.")
	@PostMapping("/login")
	public ResponseEntity<TokenResponse> login(@RequestBody LoginRequest request) {
		return ResponseEntity.ok(tokenService.login(request.userId()));
	}

	@Operation(summary = "토큰 재발급", description = "Redis에 저장된 refresh token을 검증한 뒤 새 토큰을 발급합니다.")
	@PostMapping("/refresh")
	public ResponseEntity<TokenResponse> refresh(@RequestBody RefreshTokenRequest request) {
		return ResponseEntity.ok(tokenService.refresh(request.userId(), request.refreshToken()));
	}

	@Operation(summary = "로그아웃", description = "access token을 블랙리스트에 넣고 refresh token을 제거합니다.")
	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(@RequestBody LogoutRequest request) {
		tokenService.logout(request.userId(), request.accessToken());
		return ResponseEntity.ok(Map.of("message", "logout success"));
	}

	@Operation(summary = "블랙리스트 확인", description = "해당 access token이 로그아웃 처리되었는지 확인합니다.")
	@GetMapping("/blacklist/{accessToken}")
	public ResponseEntity<Map<String, Object>> checkBlacklist(@PathVariable String accessToken) {
		return ResponseEntity.ok(Map.of(
			"accessToken", accessToken,
			"blacklisted", tokenService.isBlacklisted(accessToken)
		));
	}
}
