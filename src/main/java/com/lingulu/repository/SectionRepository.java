package com.lingulu.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.lingulu.entity.Section;

public interface SectionRepository extends JpaRepository<Section,UUID>{
    
} 
