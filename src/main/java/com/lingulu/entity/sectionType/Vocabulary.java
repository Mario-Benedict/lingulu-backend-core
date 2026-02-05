package com.lingulu.entity.sectionType;


import com.lingulu.entity.Section;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "vocabulary")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vocabulary{

    @Id
    @GeneratedValue
    @Column(name = "vocab_id")
    private UUID vocabId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "word")
    private String word;

    @Column(name = "translation")
    private String translation;

    @Column(name = "audio_path")
    private String audioPath;
}
