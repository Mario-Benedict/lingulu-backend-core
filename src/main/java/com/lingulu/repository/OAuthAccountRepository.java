package com.lingulu.repository;

import com.lingulu.entity.account.OAuthAccount;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface OAuthAccountRepository extends JpaRepository<OAuthAccount, UUID>{
    OAuthAccount findByUser_UserId(UUID userId);
    
} 
