package com.lingulu.dto.sectionContent;


import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SpeakingContent {
    private UUID speakingId;
    private String sentence;
    private String audioPath;
}
