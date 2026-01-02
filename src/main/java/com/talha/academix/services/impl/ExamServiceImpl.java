package com.talha.academix.services.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.CreateExamRequest;
import com.talha.academix.dto.ExamResponse;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Exam;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.ExamService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepo examRepo;
    private final CourseRepo courseRepo;
    private final CourseService courseService;

    @Override
    public ExamResponse createExam(Long teacherId, CreateExamRequest req) {
        if (!courseService.teacherOwnership(teacherId, req.getCourseId())) {
            throw new RoleMismatchException("Only teacher can create exam for this course.");
        }

        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));

        Exam exam = new Exam();
        exam.setTitle(req.getTitle());
        exam.setCourse(course);

        exam = examRepo.save(exam);
        return toResponse(exam);
    }

    @Override
    public ExamResponse updateExam(Long teacherId, Long examId, CreateExamRequest req) {
        Exam existing = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (!courseService.teacherOwnership(teacherId, existing.getCourse().getCourseId())) {
            throw new RoleMismatchException("Only teacher can update exam.");
        }

        if (req.getTitle() != null) {
            existing.setTitle(req.getTitle());
        }

        existing = examRepo.save(existing);
        return toResponse(existing);
    }

    @Override
    public ExamResponse getExamById(Long examId) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        return toResponse(exam);
    }

    @Override
    public List<ExamResponse> getExamsByCourse(Long courseId) {
        return examRepo.findByCourseId(courseId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public void deleteExam(Long teacherId, Long examId) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (!courseService.teacherOwnership(teacherId, exam.getCourse().getCourseId())) {
            throw new RoleMismatchException("Only teacher can delete exam.");
        }

        examRepo.delete(exam);
    }


    private ExamResponse toResponse(Exam exam) {
        return new ExamResponse(
                exam.getId(),
                exam.getTitle(),
                exam.getCourse() != null ? exam.getCourse().getCourseId() : null);
    }
}