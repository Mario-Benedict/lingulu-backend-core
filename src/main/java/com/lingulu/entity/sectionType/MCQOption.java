package com.lingulu.entity.sectionType;


import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "mcq_options")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MCQOption {

    @Id
    @GeneratedValue
    @Column(name = "option_id")
    private UUID optionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private MCQQuestion question;

    @Column(name = "option_text")
    private String optionText;

    @Column(name = "is_correct")
    private Boolean isCorrect;
}
