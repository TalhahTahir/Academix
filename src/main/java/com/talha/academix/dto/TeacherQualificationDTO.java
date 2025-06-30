package com.talha.academix.dto;

import com.talha.academix.enums.Degree;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherQualificationDTO {
    private Long degreeId;
    private Long teacherId;
    private Degree degree;
    private String institute;
    private Integer year;
}
