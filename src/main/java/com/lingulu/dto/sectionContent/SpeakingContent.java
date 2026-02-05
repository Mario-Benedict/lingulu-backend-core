package com.lingulu.dto.sectionContent;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class SpeakingContent {
    private String sentence;
    private String audioPath;
}
