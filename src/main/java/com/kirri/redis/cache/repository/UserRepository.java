package com.kirri.redis.cache.repository;

import com.kirri.redis.cache.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
