package com.lingulu.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import com.lingulu.enums.ProgressStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LessonsResponse {
    private UUID lessonId;
    private ProgressStatus status;
    private LocalDateTime completedAt;
}
