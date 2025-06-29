package com.talha.academix.controllers;

import com.talha.academix.dto.ContentDTO;
import com.talha.academix.services.ContentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contents")
@RequiredArgsConstructor
public class ContentController {

    private final ContentService contentService;

    @PostMapping
    public ContentDTO addContent(@RequestBody ContentDTO dto) {
        return contentService.addContent(dto);
    }

    @PutMapping("/{contentId}")
    public ContentDTO updateContent(@PathVariable Long contentId, @RequestBody ContentDTO dto) {
        return contentService.updateContent(contentId, dto);
    }

    @GetMapping("/{contentId}")
    public ContentDTO getContentById(@PathVariable Long contentId) {
        return contentService.getContentById(contentId);
    }

    @GetMapping("/course/{courseId}")
    public List<ContentDTO> getContentByCourse(@PathVariable Long courseId) {
        return contentService.getContentByCourse(courseId);
    }

    @DeleteMapping("/{contentId}")
    public void deleteContent(@PathVariable Long contentId) {
        contentService.deleteContent(contentId);
    }
}
