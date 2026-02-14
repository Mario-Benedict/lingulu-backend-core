package com.lingulu.dto.sectionContent;

import java.util.UUID;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GrammarContent {
    private UUID grammarId;
    private String title;
    private String filePath;
}