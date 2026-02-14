package com.lingulu.entity.course;

import java.util.List;

import com.lingulu.entity.sectionType.Vocabulary;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "section_vocabulary")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionVocabulary extends Section {
    @OneToMany(mappedBy = "sectionVocabulary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vocabulary> vocabularies;   
}
