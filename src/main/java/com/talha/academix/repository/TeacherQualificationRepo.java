package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.Degree;
import com.talha.academix.model.TeacherQualification;

public interface TeacherQualificationRepo extends JpaRepository<TeacherQualification, Long> {

    List<TeacherQualification> findByDegree(Degree degree);

    public List<TeacherQualification> findByTeacherID(Long teacherId);
}
