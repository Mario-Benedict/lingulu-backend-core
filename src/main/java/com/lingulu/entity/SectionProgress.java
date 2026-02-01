package com.lingulu.entity;

import com.lingulu.enums.ProgressStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
        name = "section_progress",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "section_id"})
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class SectionProgress {

    @Id
    @GeneratedValue
    private UUID progressId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private ProgressStatus status;

    private int totalLessons;
    private int completedLessons;

}
