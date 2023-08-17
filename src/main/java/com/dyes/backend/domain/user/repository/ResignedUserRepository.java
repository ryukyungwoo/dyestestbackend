package com.dyes.backend.domain.user.repository;

import com.dyes.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResignedUserRepository extends JpaRepository<User, String> {
}
