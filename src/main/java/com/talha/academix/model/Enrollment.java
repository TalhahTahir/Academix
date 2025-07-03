package com.talha.academix.model;

import java.util.Date;

import com.talha.academix.enums.EnrollmentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private Long studentID;
    private Long courseID;
    private Date enrollmentDate;
    private EnrollmentStatus status; //enum(completed / in progress)
    private double completionPercentage;
    private float marks; 
}
