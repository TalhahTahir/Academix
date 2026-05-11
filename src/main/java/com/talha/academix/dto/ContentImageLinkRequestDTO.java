package com.talha.academix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContentImageLinkRequestDTO {
    @NotNull
    private Long teacherId;
    @NotNull
    private Long storedFileId;
}