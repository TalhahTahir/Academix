package com.talha.academix.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Content;
import com.talha.academix.model.Lecture;

public interface LectureRepo extends JpaRepository<Lecture, Long> {
    List<Lecture> findByContent(Content content);
    int countByContent(Content content);

    long countByContent_Course_Courseid(Long courseId);
}
