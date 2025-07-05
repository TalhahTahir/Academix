package com.talha.academix.repository;

import com.talha.academix.model.Lecture;
import com.talha.academix.model.Content;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LectureRepo extends JpaRepository<Lecture, Long> {
    List<Lecture> findByContent(Content content);
    int countByContent(Content content);
}
