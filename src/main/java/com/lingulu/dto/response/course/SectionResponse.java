package com.lingulu.dto.response.course;

import java.time.LocalDateTime;
import java.util.UUID;

import com.lingulu.enums.ProgressStatus;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SectionResponse {
    private UUID sectionId;
    private String sectionTitle;
    private ProgressStatus status;
    private LocalDateTime completedAt;
}
