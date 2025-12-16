package com.talha.academix.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.SignedDownloadResponseDTO;
import com.talha.academix.dto.SignedUploadInitRequestDTO;
import com.talha.academix.services.StoredFileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class StoredFileController {

    private final StoredFileService storedFileService;

    // Step 1: backend generates signed upload URL for private bucket
    @PostMapping("/initiate-signed-upload")
    public ResponseEntity initiateSignedUpload(@RequestBody SignedUploadInitRequestDTO req) {
        return ResponseEntity.ok(storedFileService.initiateSignedUpload(req));
    }

    // Step 3: after successful upload via Postman, mark READY
    @PostMapping("/{id}/mark-ready")
    public ResponseEntity markReady(@PathVariable Long id) {
        storedFileService.markReady(id);
        return ResponseEntity.ok().build();
    }

    // Step 4: get signed download URL (for playing video/downloading doc)
    @GetMapping("/{id}/signed-download")
    public ResponseEntity<SignedDownloadResponseDTO> signedDownload(
            @PathVariable Long id,
            @RequestParam(defaultValue = "600") int expiresIn
    ) {
        return ResponseEntity.ok(storedFileService.getSignedDownloadUrl(id, expiresIn));
    }
}