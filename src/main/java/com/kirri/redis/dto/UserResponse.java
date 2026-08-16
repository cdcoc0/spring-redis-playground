package com.kirri.redis.dto;

import java.io.Serializable;

import com.kirri.redis.domain.User;

public record UserResponse(Long id, String name, int age) implements Serializable {

	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getName(), user.getAge());
	}
}
