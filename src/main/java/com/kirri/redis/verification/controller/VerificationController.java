package com.kirri.redis.verification.controller;

import java.util.Map;

import com.kirri.redis.verification.dto.VerificationConfirmRequest;
import com.kirri.redis.verification.dto.VerificationCreateRequest;
import com.kirri.redis.verification.service.VerificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Verification", description = "TTL based verification code practice API")
@RestController
@RequestMapping("/verification")
public class VerificationController {

	private final VerificationService verificationService;

	public VerificationController(VerificationService verificationService) {
		this.verificationService = verificationService;
	}

	@Operation(summary = "인증번호 발급", description = "전화번호 기준으로 인증번호를 저장하고 3분 TTL을 설정합니다.")
	@PostMapping
	public ResponseEntity<Map<String, Object>> createCode(@RequestBody VerificationCreateRequest request) {
		String code = verificationService.createCode(request.phone());

		return ResponseEntity.ok(Map.of(
			"phone", request.phone(),
			"code", code,
			"ttlSeconds", 180,
			"message", "verification code saved"
		));
	}

	@Operation(summary = "인증번호 검증", description = "저장된 인증번호와 비교하고 성공 시 즉시 삭제합니다.")
	@PostMapping("/confirm")
	public ResponseEntity<Map<String, Object>> confirmCode(@RequestBody VerificationConfirmRequest request) {
		boolean success = verificationService.verifyCode(request.phone(), request.code());

		if (!success) {
			return ResponseEntity.badRequest().body(Map.of(
				"phone", request.phone(),
				"message", "invalid or expired code"
			));
		}

		return ResponseEntity.ok(Map.of(
			"phone", request.phone(),
			"message", "verification success"
		));
	}

	@Operation(summary = "남은 TTL 조회", description = "인증번호가 몇 초 뒤 만료되는지 확인합니다.")
	@GetMapping("/{phone}/ttl")
	public ResponseEntity<Map<String, Object>> getTtl(@PathVariable String phone) {
		Long ttlSeconds = verificationService.getRemainingSeconds(phone);

		if (ttlSeconds == null || ttlSeconds < 0) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(Map.of(
			"phone", phone,
			"ttlSeconds", ttlSeconds
		));
	}
}
