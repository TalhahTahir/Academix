package com.talha.academix.model;

import java.math.BigDecimal;
import java.util.List;

import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
    private BigDecimal fees;
    
    @Enumerated(EnumType.STRING)
    private CourseCategory category;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable=false)
    private User teacher;

    @Enumerated(EnumType.STRING)
    private CourseState state;

    @OneToMany(mappedBy = "course", cascade=CascadeType.ALL, orphanRemoval=true)
    private List<Exam> exams;

    @OneToMany(mappedBy="course", cascade = CascadeType.ALL, orphanRemoval=true)
    private List<Content> contents;

    @OneToMany(mappedBy="course", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
    private List<Enrollment> enrollments;
}
