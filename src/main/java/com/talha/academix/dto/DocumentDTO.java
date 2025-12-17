package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {
    private Long documentId;
    private Long contentId;
    private String title;
    private String description;
    private Long storedFileId;
    private String fileSignedUrl;
}
