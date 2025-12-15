package com.talha.academix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.FileUploadRequestDTO;
import com.talha.academix.dto.FileUploadResponseDTO;
import com.talha.academix.services.StoredFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class StoredFileController {

    private final StoredFileService storedFileService;

    @PostMapping("/initiate")
    public ResponseEntity<FileUploadResponseDTO> initiate(
            @RequestParam Long teacherId,
            @RequestParam Long courseId,
            @RequestBody FileUploadRequestDTO req
    ) {
        return ResponseEntity.ok(storedFileService.initiateUpload(teacherId, courseId, req));
    }

    @PostMapping("/{id}/mark-ready")
    public ResponseEntity<Void> markReady(@PathVariable Long id) {
        storedFileService.markReady(id);
        return ResponseEntity.ok().build();
    }
}