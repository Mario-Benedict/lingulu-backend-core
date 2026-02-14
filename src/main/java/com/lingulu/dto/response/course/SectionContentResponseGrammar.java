package com.lingulu.dto.response.course;

import java.util.List;

import com.lingulu.dto.sectionContent.GrammarContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionContentResponseGrammar extends SectionContentResponse{
    private List<GrammarContent> grammar;
}
