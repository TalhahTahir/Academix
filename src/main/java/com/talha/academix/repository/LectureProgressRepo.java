package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.LectureProgress;

import jakarta.persistence.LockModeType;

public interface LectureProgressRepo extends JpaRepository<LectureProgress, Long> {
    Optional<LectureProgress> findByEnrollmentIdAndLectureId(Long enrollmentId, Long lectureId);
    List<LectureProgress> findByEnrollmentId(Long enrollmentId);

     @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lp FROM LectureProgress lp WHERE lp.enrollment.enrollmentID = :enrollmentId AND lp.lecture.lectureId = :lectureId")
    Optional<LectureProgress> findByEnrollmentIdAndLectureIdForUpdate(@Param("enrollmentId") Long enrollmentId,
                                                                     @Param("lectureId") Long lectureId);

    @Query("SELECT COUNT(lp) FROM LectureProgress lp WHERE lp.enrollment.enrollmentID = :enrollmentId AND lp.completed = true AND lp.lecture.content.course.courseid = :courseId")
    long countCompletedByEnrollmentAndCourse(@Param("enrollmentId") Long enrollmentId, @Param("courseId") Long courseId);

}
