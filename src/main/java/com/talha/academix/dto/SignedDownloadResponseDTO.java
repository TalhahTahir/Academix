package com.talha.academix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignedDownloadResponseDTO {
    private Long storedFileId;
    private String bucket;
    private String objectKey;
    private String signedDownloadUrl;
    private int expiresIn;
}