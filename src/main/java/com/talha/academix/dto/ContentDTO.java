package com.talha.academix.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {

    private Long contentId;
    @NotNull
    private Long courseId;
    @NotBlank
    private String description;
    private Long imageFileId;
    private String imageSignedUrl;
}
