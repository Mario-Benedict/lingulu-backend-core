package com.lingulu.entity.course;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import com.lingulu.entity.sectionType.Grammar;
import com.lingulu.entity.sectionType.MCQQuestion;
import com.lingulu.entity.sectionType.Speaking;
import com.lingulu.entity.sectionType.Vocabulary;
import com.lingulu.enums.SectionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "section")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Section {

    @Id
    @GeneratedValue
    @Column(name = "section_id")
    private UUID sectionId;

    @Column(name = "section_title")
    private String sectionTitle;

    @Column(name = "section_type")
    @Enumerated(EnumType.STRING)
    private SectionType sectionType;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "lesson_id", nullable = false)
    private Lesson lesson;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SectionProgress> sectionProgresses;

    @OneToOne(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private Grammar grammar;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Vocabulary> vocabularies;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Speaking> speakings;

    @OneToMany(mappedBy = "section", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MCQQuestion> mcqQuestions;

}
