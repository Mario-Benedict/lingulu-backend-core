package com.lingulu.entity.course;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import com.lingulu.enums.SectionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "sections")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@SuperBuilder
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

    @Column(name = "position")
    private Integer position;

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

}
