package com.lingulu.entity.sectionType;


import com.lingulu.entity.course.Section;
import com.lingulu.entity.course.SectionVocabulary;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "vocabularies")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vocabulary{

    @Id
    @GeneratedValue
    @Column(name = "vocab_id")
    private UUID vocabId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private SectionVocabulary sectionVocabulary;

    @Column(name = "word")
    private String word;

    @Column(name = "translation")
    private String translation;

    @Column(name = "vocab_audio_path")
    private String audioPath;
}
