package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDTO {

    private Long documentId;
    @NotNull
    private Long contentId;
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private Long storedFileId;
    private String fileSignedUrl;
}
