package com.talha.academix.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.talha.academix.model.Attempt;
import com.talha.academix.model.Exam;

public interface AttemptRepo extends JpaRepository<Attempt, Long>{

    @Query("SELECT a FROM Attempt a WHERE a.student.userid = :studentId")
    List<Attempt> findByStudentId(@Param("studentId") Long studentId);

    // Optional convenience (uses property path resolution)
    List<Attempt> findByStudent_Userid(Long studentId);

    @Query("SELECT a FROM Attempt a WHERE a.exam.id = :examId")
    List<Attempt> findByExamId(@Param("examId") Long examId);

    Optional<Attempt> findByExamIdAndStudentUserid(Long examId, Long studentId);

    boolean existsByAttemptIdAndStudent_Userid(Long attemptId, Long Userid);

    Optional<Attempt> findByExam_IdAndStudent_Userid(Long examId, Long studentId);
    
}
