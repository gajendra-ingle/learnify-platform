package com.learnify.auth.repository;

import com.learnify.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    void updateLastLoginAt(UUID eq, Object any);

    Object existsByUsername(String testuser);
}

