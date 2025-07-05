package com.talha.academix.repository;

import com.talha.academix.model.TeacherQualification;
import com.talha.academix.model.User;
import com.talha.academix.enums.Degree;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeacherQualificationRepo extends JpaRepository<TeacherQualification, Long> {
    List<TeacherQualification> findByTeacher(User teacher);
    List<TeacherQualification> findByDegree(Degree degree);
}
