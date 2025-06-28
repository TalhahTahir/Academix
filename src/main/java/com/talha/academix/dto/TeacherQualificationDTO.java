package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherQualificationDTO {
    private Long degreeId;
    private Long teacherId;
    private String degreeName;
    private String institute;
    private Integer year;
}
