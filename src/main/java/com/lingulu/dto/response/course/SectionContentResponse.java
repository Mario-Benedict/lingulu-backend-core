package com.lingulu.dto.response.course;
import com.lingulu.enums.SectionType;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class SectionContentResponse {

    private UUID sectionId;
    private SectionType sectionType;
    private String sectionTitle;
    private Boolean isCompleted;
}
