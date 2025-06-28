package com.talha.academix.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.talha.academix.model.Lecture;

public interface LectureRepo extends JpaRepository<Lecture, Long> {
}
