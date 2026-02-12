package com.lingulu.entity.sectionType;

import com.lingulu.entity.Section;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mcq_question")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCQQuestion {

    @Id
    @GeneratedValue
    @Column(name = "question_id")
    private UUID questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "question_text")
    private String questionText;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MCQOption> options;
}

