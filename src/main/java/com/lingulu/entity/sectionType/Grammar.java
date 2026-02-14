package com.lingulu.entity.sectionType;

import com.lingulu.entity.course.Section;
import com.lingulu.entity.course.SectionGrammar;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "grammar")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grammar {

    @Id
    @Column(name = "section_id", nullable = false)
    private UUID sectionId;

    @ManyToOne
    @MapsId
    @JoinColumn(name = "section_id")
    private SectionGrammar sectionGrammar;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath;

}
