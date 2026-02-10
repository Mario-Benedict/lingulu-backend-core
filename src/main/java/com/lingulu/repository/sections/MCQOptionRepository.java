package com.lingulu.repository.sections;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.sectionType.MCQOption;

public interface MCQOptionRepository extends JpaRepository<MCQOption, UUID> {

    MCQOption findByOptionId(UUID optionId);
    
}