package com.lingulu.entity.sectionType;


import com.lingulu.entity.course.Section;
import com.lingulu.entity.course.SectionSpeaking;

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
    private SectionSpeaking sectionSpeaking;

    @Column(name = "sentence")
    private String sentence;

    @Column(name = "speaking_audio_path")
    private String speakingAudioPath;
}
