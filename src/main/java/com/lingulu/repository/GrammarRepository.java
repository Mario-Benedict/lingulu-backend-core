package com.lingulu.repository;

import com.lingulu.entity.sectionType.Grammar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface GrammarRepository extends JpaRepository<Grammar, UUID> {
}

