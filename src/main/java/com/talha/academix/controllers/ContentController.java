package com.talha.academix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.dto.ContentImageLinkRequestDTO;
import com.talha.academix.services.ContentService;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PreAuthorize("hasRole('TEACHER') and @courseSecurity.isCourseOwner(principal, #dto.courseId)")
    @PostMapping("/{teacherId}")
    public ResponseEntity<ContentDTO> createContent(@PathVariable Long teacherId, @RequestBody ContentDTO dto) {
        return ResponseEntity.ok(contentService.addContent(teacherId, dto));
    }

    @PreAuthorize("hasRole('TEACHER') and @contentSecurity.isContentOwner(principal, #contentId)")
    @PutMapping("/{contentId}/image")
    public ResponseEntity<ContentDTO> setContentImage(@PathVariable Long contentId,
            @RequestBody ContentImageLinkRequestDTO req) {
        return ResponseEntity.ok(contentService.setContentImage(req.getTeacherId(), contentId, req.getStoredFileId()));
    }

    @PreAuthorize("hasRole('TEACHER') and @contentSecurity.isContentOwner(principal, #contentId)")
    @PutMapping("/update/{contentId}/teachers/{teacherId}")
    public ContentDTO updateContent(@PathVariable Long contentId, @PathVariable Long teacherId, @RequestBody ContentDTO dto) {
        return contentService.updateContent(teacherId, contentId, dto);
    }

    @PreAuthorize("hasRole('TEACHER') and @contentSecurity.isContentOwner(principal, #contentId)")
    @DeleteMapping("/{contentId}/teachers/{teacherId}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long contentId, @PathVariable Long teacherId) {
        contentService.deleteContent(teacherId, contentId);
        return ResponseEntity.noContent().build();
    }
}