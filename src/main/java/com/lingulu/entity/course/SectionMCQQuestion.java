package com.lingulu.entity.course;

import java.util.List;

import com.lingulu.entity.sectionType.MCQQuestion;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "section_mcq_question")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SectionMCQQuestion extends Section{
    @OneToMany(mappedBy = "sectionMcqQuestion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MCQQuestion> mcqQuestions;
}
