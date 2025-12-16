package com.talha.academix.dto;

import com.talha.academix.enums.StoredFileType;
import lombok.Data;

@Data
public class SignedUploadInitRequestDTO {
    private Long teacherId;
    private Long courseId; // CHANGED (was contentId)

    private String fileName;
    private String mimeType;
    private Long sizeBytes;
    private StoredFileType type; // LECTURE or DOCUMENT
}