package com.talha.academix.model;

import com.talha.academix.enums.CourseAvailability;
import com.talha.academix.enums.CourseCatagory;

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
@Table(name="course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long courseid;
    @NotEmpty
    private String coursename;
    @NotEmpty
    private String duration;
    @NotEmpty
    private Integer fees;

    private CourseAvailability availability;
    private CourseCatagory catagory;
    private Long teacherid;
    private Long examid;
    private Long contentid;
}
