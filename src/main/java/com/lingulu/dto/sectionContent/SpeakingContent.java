package com.lingulu.dto.sectionContent;


import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingContent {
    private String sentence;
    private String audioPath;
}
