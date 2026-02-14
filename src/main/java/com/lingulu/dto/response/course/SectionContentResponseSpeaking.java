package com.lingulu.dto.response.course;

import java.util.List;

import com.lingulu.dto.sectionContent.SpeakingContent;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class SectionContentResponseSpeaking extends SectionContentResponse{
    private List<SpeakingContent> speakings;
}
