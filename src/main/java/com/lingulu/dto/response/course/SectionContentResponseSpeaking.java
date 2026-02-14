package com.lingulu.dto.response.course;

import java.util.List;

import com.lingulu.dto.sectionContent.SpeakingContent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SectionContentResponseSpeaking extends SectionContentResponse{
    private List<SpeakingContent> speakings;
}
