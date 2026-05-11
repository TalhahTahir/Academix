package com.talha.academix.dto;

import java.time.Instant;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {

    private Long certificateId;
    @NotNull
    private Long studentId;
    @NotNull
    private Long teacherId;
    @NotNull
    private Long courseId;
    @NotNull
    private Instant date;
}
