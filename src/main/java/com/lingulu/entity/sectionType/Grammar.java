package com.lingulu.entity.sectionType;

import com.lingulu.entity.Section;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "grammar")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Grammar {

    @Id
    @Column(name = "section_id", nullable = false)
    private UUID sectionId;

    @OneToOne
    @MapsId
    @JoinColumn(name = "section_id")
    private Section section;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

}
