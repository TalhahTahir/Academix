package com.talha.academix.services;

import com.talha.academix.dto.SignedDownloadResponseDTO;
import com.talha.academix.dto.SignedUploadInitRequestDTO;
import com.talha.academix.dto.SignedUploadInitResponseDTO;

public interface StoredFileService {
    SignedUploadInitResponseDTO initiateSignedUpload(SignedUploadInitRequestDTO req);
    void markReady(Long storedFileId);
    SignedDownloadResponseDTO getSignedDownloadUrl(Long storedFileId, int expiresInSeconds);
}