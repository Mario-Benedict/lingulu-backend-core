package com.lingulu.repository;

import com.lingulu.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

import javax.swing.text.html.Option;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User> findByUserId(UUID userId);

    // Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);
}