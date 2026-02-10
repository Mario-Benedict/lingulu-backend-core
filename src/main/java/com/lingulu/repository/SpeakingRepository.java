package com.lingulu.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.sectionType.Speaking;

public interface SpeakingRepository extends JpaRepository<Speaking, UUID>{
    Speaking findByExerciseId(UUID exerciseId);
    
}