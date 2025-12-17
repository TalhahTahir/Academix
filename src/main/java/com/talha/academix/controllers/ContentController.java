package com.talha.academix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.dto.ContentImageLinkRequestDTO;
import com.talha.academix.services.ContentService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PutMapping("/{contentId}/image")
    public ResponseEntity<ContentDTO> setContentImage(
            @PathVariable Long contentId,
            @RequestBody ContentImageLinkRequestDTO req
    ) {
        return ResponseEntity.ok(
                contentService.setContentImage(req.getTeacherId(), contentId, req.getStoredFileId())
        );
    }
}