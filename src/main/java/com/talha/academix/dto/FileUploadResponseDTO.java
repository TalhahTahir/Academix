package com.talha.academix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponseDTO {
    private Long storedFileId;
    private String bucket;
    private String objectKey;

    // For Path 1 (Supabase): this is the direct upload URL (object endpoint).
    private String uploadUrl;

    // Client must include "Authorization: Bearer <SUPABASE_ANON_KEY>" for now
    private String requiredAuthHeaderExample;
}