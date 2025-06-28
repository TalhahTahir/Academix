package com.talha.academix.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "teacher_qualification")
public class TeacherQualification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long degreeId;

    @NotEmpty
    private Long teacherId;

    private String degreeName;

    private String institute;

    private Integer year;
}
