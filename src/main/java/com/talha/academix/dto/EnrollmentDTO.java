package com.talha.academix.dto;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.talha.academix.enums.EnrollmentStatus;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {

    private Long enrollmentId;
    @NotNull
    private Long studentId;
    @NotNull
    private Long courseId;
    @NotNull
    private Instant enrollmentDate;
    @NotNull
    private EnrollmentStatus status;
    @NotNull
    private Double completionPercentage;
    @NotNull
    private Double marks;
}
