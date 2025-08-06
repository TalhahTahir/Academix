package com.talha.academix.model;

import java.util.List;

import com.talha.academix.enums.CourseCatagory;
import com.talha.academix.enums.CourseState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
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
    
    private CourseCatagory catagory;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable=false)
    private User teacher;

    private CourseState state;

    @OneToMany(mappedBy = "course", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Exam> exams;

    @OneToMany(mappedBy="course", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Content> contents;

    @OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Enrollment> enrollments;
}
