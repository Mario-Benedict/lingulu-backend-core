package com.lingulu.entity.sectionType;


import com.lingulu.entity.course.Section;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "speaking")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Speaking {

    @Id
    @GeneratedValue
    @Column(name = "exercise_id")
    private UUID exerciseId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private Section section;

    @Column(name = "sentence")
    private String sentence;

    @Column(name = "audio_path")
    private String audioPath;
}
