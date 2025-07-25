package com.talha.academix.dto;

import java.time.ZonedDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CertificateDTO {
    private Long certificateId;
    private Long studentId;
    private Long teacherId;
    private Long courseId;
    private Double marks;
    private ZonedDateTime date;
}
