package com.lingulu.entity.course;

import java.util.List;

import com.lingulu.entity.sectionType.Grammar;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "section_grammar")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionGrammar extends Section {
    @OneToMany(mappedBy = "sectionGrammar", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Grammar> grammars;
}   
