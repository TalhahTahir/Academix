package com.talha.academix.repository;

import com.talha.academix.model.TeachingRequest;
import com.talha.academix.model.User;
import com.talha.academix.model.Course;
import com.talha.academix.enums.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TeachingRequestRepo extends JpaRepository<TeachingRequest, Long> {
    List<TeachingRequest> findByTeacher(User teacher);
    List<TeachingRequest> findByCourse(Course course);
    List<TeachingRequest> findByStatus(RequestStatus status);
}
