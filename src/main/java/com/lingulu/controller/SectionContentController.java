package com.lingulu.controller;

import com.lingulu.dto.ApiResponse;
import com.lingulu.dto.SectionContentResponse;
import com.lingulu.service.SectionContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/sections")
@RequiredArgsConstructor
public class SectionContentController {

    private final SectionContentService sectionContentService;

    @GetMapping("/{sectionId}/content")
    public ResponseEntity<ApiResponse<SectionContentResponse>> getSectionContent(
            @PathVariable UUID sectionId
    ) {
        SectionContentResponse response =
                sectionContentService.getSectionContent(sectionId);

        return ResponseEntity.ok(
                new ApiResponse<>(true, "Section content fetched", response)
        );
    }
}
