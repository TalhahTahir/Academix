package com.talha.academix.model;

import java.util.Date;

import com.talha.academix.enums.EnrollmentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "enrollment")
public class Enrollment {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Long enrollmentID;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="student_id", nullable=false)
    private User student;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="Course_id", nullable=false)
    private Course course;


    private Date enrollmentDate;

    @Enumerated(EnumType.STRING)
    private EnrollmentStatus status; //enum(completed / in progress)

    private double completionPercentage;
    
    private double marks; 
}
