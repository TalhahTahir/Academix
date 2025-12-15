package com.talha.academix.services;

import com.talha.academix.dto.FileUploadRequestDTO;
import com.talha.academix.dto.FileUploadResponseDTO;

public interface StoredFileService {
    FileUploadResponseDTO initiateUpload(Long teacherId, Long courseId, FileUploadRequestDTO req);
    void markReady(Long storedFileId);
}