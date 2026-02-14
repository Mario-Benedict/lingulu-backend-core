package com.lingulu.dto.response.course;

import com.lingulu.dto.sectionContent.McqContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SectionContentResponseMcqContent extends SectionContentResponse{
    private McqContent mcq;
}
