package com.kirri.redis.controller;

import com.kirri.redis.dto.UserCreateRequest;
import com.kirri.redis.dto.UserResponse;
import com.kirri.redis.dto.UserUpdateRequest;
import com.kirri.redis.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "User Cache", description = "MySQL 조회 결과를 Redis에 캐싱하는 연습 API")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

	private final UserService userService;

	@Operation(summary = "사용자 생성", description = "MySQL users 테이블에 연습용 데이터를 저장합니다.")
	@PostMapping
	public ResponseEntity<UserResponse> createUser(@RequestBody UserCreateRequest request) {
		return ResponseEntity.ok(userService.createUser(request));
	}

	@Operation(summary = "사용자 단건 조회", description = "첫 요청은 MySQL, 같은 요청의 재호출은 Redis 캐시를 사용합니다.")
	@GetMapping("/{id}")
	public ResponseEntity<UserResponse> getUser(@PathVariable Long id) {
		return ResponseEntity.ok(userService.getUser(id));
	}

	@Operation(summary = "사용자 수정", description = "수정 후 기존 캐시를 비워 다음 조회에서 다시 MySQL을 타게 합니다.")
	@PatchMapping("/{id}")
	public ResponseEntity<UserResponse> updateUser(@PathVariable Long id, @RequestBody UserUpdateRequest request) {
		return ResponseEntity.ok(userService.updateUser(id, request));
	}
}
