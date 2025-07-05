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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;
    
    @Enumerated(EnumType.STRING)
    private ProgressStatus status; // COMPLETED or IN_PROGRESS
}

