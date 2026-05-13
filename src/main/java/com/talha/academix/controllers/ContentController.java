package com.talha.academix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.dto.ContentImageLinkRequestDTO;
import com.talha.academix.services.ContentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PreAuthorize("@courseSecurity.isCourseOwner(principal, #dto.courseId)")
    @PostMapping
    public ResponseEntity<ContentDTO> createContent(@Valid @RequestBody ContentDTO dto) {
        return ResponseEntity.ok(contentService.addContent(dto));
    }

    @PreAuthorize("@contentSecurity.isContentOwner(principal, #contentId)")
    @PutMapping("/{contentId}/image")
    public ResponseEntity<ContentDTO> setContentImage(@PathVariable Long contentId,
            @Valid @RequestBody ContentImageLinkRequestDTO req) {
        return ResponseEntity.ok(contentService.setContentImage(contentId, req.getStoredFileId()));
    }

    @PreAuthorize("@contentSecurity.isContentOwner(principal, #contentId)")
    @PutMapping("/update/{contentId}")
    public ContentDTO updateContent(@PathVariable Long contentId, @PathVariable Long teacherId, @Valid @RequestBody ContentDTO dto) {
        return contentService.updateContent(contentId, dto);
    }

    @PreAuthorize("@contentSecurity.isContentOwner(principal, #contentId)")
    @DeleteMapping("/{contentId}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long contentId, @PathVariable Long teacherId) {
        contentService.deleteContent(contentId);
        return ResponseEntity.noContent().build();
    }
}