package com.talha.academix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignedUploadInitResponseDTO {
    private Long storedFileId;
    private String bucket;
    private String objectKey;

    // upload via signed URL (works for private bucket)
    private String signedUploadUrl;

    // How long it stays valid (seconds)
    private int expiresIn;

    // Postman should PUT binary to signedUploadUrl with Content-Type header
}