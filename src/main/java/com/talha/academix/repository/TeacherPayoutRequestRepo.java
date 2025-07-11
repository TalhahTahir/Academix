package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.enums.RequestStatus;
import com.talha.academix.model.TeacherPayoutRequest;

public interface TeacherPayoutRequestRepo extends JpaRepository<TeacherPayoutRequest, Long> {
    List<TeacherPayoutRequest> findByStatus(RequestStatus status);
    List<TeacherPayoutRequest> findByTeacherId(Long teacherId);
}

