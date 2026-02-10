package com.lingulu.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.lingulu.entity.sectionType.Vocabulary;

public interface VocabularyRepository extends JpaRepository<Vocabulary, UUID> {
}
