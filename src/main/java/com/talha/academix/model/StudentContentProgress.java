package com.talha.academix.model;

import com.talha.academix.enums.ProgressStatus;

import jakarta.persistence.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "student_content_progress")
public class StudentContentProgress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long studentId; // userId of student
    private Long courseId;  // redundancy to ease queries

    private Long contentId;
    
    @Enumerated(EnumType.STRING)
    private ProgressStatus status; // COMPLETED or IN_PROGRESS
}

