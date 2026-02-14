package com.lingulu.entity.sectionType;

import com.lingulu.entity.course.Section;
import com.lingulu.entity.course.SectionGrammar;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "grammars")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Grammar {

    @Id
    @Column(name = "grammar_id", nullable = false)
    private UUID grammarId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "section_id", nullable = false)
    private SectionGrammar sectionGrammar;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "markdown_file_path", nullable = false, columnDefinition = "TEXT")
    private String filePath;

}
