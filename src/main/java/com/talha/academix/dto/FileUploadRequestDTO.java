package com.talha.academix.dto;

import com.talha.academix.enums.StoredFileType;
import lombok.Data;

@Data
public class FileUploadRequestDTO {
    private String fileName;
    private String mimeType;
    private Long sizeBytes;
    private StoredFileType type; // LECTURE_VIDEO or DOCUMENT_FILE
}