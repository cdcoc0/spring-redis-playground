package com.kirri.redis.cache.service;

import com.kirri.redis.cache.domain.User;
import com.kirri.redis.cache.dto.UserCreateRequest;
import com.kirri.redis.cache.dto.UserResponse;
import com.kirri.redis.cache.dto.UserUpdateRequest;
import com.kirri.redis.cache.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

	private static final Logger log = LoggerFactory.getLogger(UserService.class);

	private final UserRepository userRepository;

	@Transactional
	public UserResponse createUser(UserCreateRequest request) {
		User user = userRepository.save(new User(request.name(), request.age()));
		return UserResponse.from(user);
	}

	@Cacheable(cacheNames = "users", key = "#userId")
	@Transactional(readOnly = true)
	public UserResponse getUser(Long userId) {
		// If this log appears, the data came from MySQL rather than Redis cache.
		log.info("Loading user {} from MySQL", userId);

		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found. id=" + userId));

		return UserResponse.from(user);
	}

	@CacheEvict(cacheNames = "users", key = "#userId")
	@Transactional
	public UserResponse updateUser(Long userId, UserUpdateRequest request) {
		User user = userRepository.findById(userId)
			.orElseThrow(() -> new IllegalArgumentException("User not found. id=" + userId));

		if (request.name() != null && !request.name().isBlank()) {
			user.changeName(request.name());
		}

		if (request.age() != null) {
			user.changeAge(request.age());
		}

		return UserResponse.from(user);
	}
}
