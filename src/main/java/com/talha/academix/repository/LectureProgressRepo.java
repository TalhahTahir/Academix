package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.LectureProgress;

public interface LectureProgressRepo extends JpaRepository<LectureProgress, Long> {
    Optional<LectureProgress> findByEnrollmentIdAndLectureId(Long enrollmentId, Long lectureId);
    List<LectureProgress> findByEnrollmentId(Long enrollmentId);
}
