package com.lingulu.entity.course;

import java.util.List;

import com.lingulu.entity.sectionType.Speaking;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "section_speaking")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionSpeaking extends Section{
    @OneToMany(mappedBy = "sectionSpeaking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Speaking> speakings;
}
