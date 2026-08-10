package com.kirri.redis.controller;

import java.util.Map;

import com.kirri.redis.dto.RedisSetRequest;
import com.kirri.redis.service.RedisService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Redis", description = "RedisTemplate set/get practice API")
@RestController
@RequestMapping("/redis")
public class RedisController {

	private final RedisService redisService;

	public RedisController(RedisService redisService) {
		this.redisService = redisService;
	}

	@Operation(summary = "Redis에 문자열 저장")
	@PostMapping
	public ResponseEntity<Map<String, String>> setValue(@RequestBody RedisSetRequest request) {
		redisService.set(request.key(), request.value());
		return ResponseEntity.ok(Map.of(
			"key", request.key(),
			"value", request.value(),
			"message", "saved"
		));
	}

	@Operation(summary = "Redis에서 문자열 조회")
	@GetMapping("/{key}")
	public ResponseEntity<Map<String, String>> getValue(@PathVariable String key) {
		String value = redisService.get(key);

		if (value == null) {
			return ResponseEntity.notFound().build();
		}

		return ResponseEntity.ok(Map.of(
			"key", key,
			"value", value
		));
	}
}
