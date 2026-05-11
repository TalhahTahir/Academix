package com.talha.academix.dto;

import com.talha.academix.enums.StoredFileType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FileUploadRequestDTO {
    @NotBlank
    private String fileName;
    @NotBlank
    private String mimeType;
    @NotNull
    private Long sizeBytes;
    @NotNull
    private StoredFileType type; // LECTURE_VIDEO or DOCUMENT_FILE
}