package com.talha.academix.services.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Attempt;
import com.talha.academix.model.AttemptAnswer;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.Exam;
import com.talha.academix.repository.AttemptAnswerRepo;
import com.talha.academix.repository.AttemptRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.AttemptService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepo attemptRepo;
    private final ExamRepo examRepo;
    private final AttemptAnswerRepo attemptAnswerRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final ActivityLogService activityLogService;
    private final ModelMapper modelMapper;

    @Override
    public AttemptDTO startAttempt(Long examId, Long studentId) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        Attempt attempt = new Attempt();
        attempt.setExam(exam);
        attempt.setStudentId(studentId);
        attempt.setStartedAt(LocalDateTime.now());
        attempt = attemptRepo.save(attempt);

        activityLogService.logAction(
                studentId,
                ActivityAction.EXAM_ATTEMPT,
                "Student " + studentId + " started attempt " + attempt.getId() + " for Exam " + examId);

        return modelMapper.map(attempt, AttemptDTO.class);
    }

    @Override
    public List<AttemptDTO> getAttemptsByStudent(Long studentId) {
        return attemptRepo.findByStudentId(studentId)
                .stream()
                .map(a -> modelMapper.map(a, AttemptDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public void completeAttempt(Long attemptId) {
        Attempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));
        attempt.setCompletedAt(LocalDateTime.now());
        attemptRepo.save(attempt);
    }

    @Override
    @Transactional
    public AttemptDTO submitAttempt(Long attemptId, AttemptDTO dto) {
        // Fetch attempt by ID
        Attempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        // Ensure attempt isn't already submitted
        if (attempt.getCompletedAt() != null) {
            throw new IllegalStateException("This attempt is already submitted.");
        }

        // Load all answers linked to this attempt
        List<AttemptAnswer> answers = attemptAnswerRepo.findByAttemptId(attemptId);

        if (answers.isEmpty()) {
            throw new IllegalStateException("Cannot submit an attempt without answers.");
        }

        // Calculate marks
        long totalQuestions = answers.size();
        long correctAnswers = answers.stream()
                .filter(ans -> ans.getSelectedOption().isCorrect())
                .count();

        float percentage = ((float) correctAnswers / totalQuestions) * 100;

        // Mark attempt as completed
        attempt.setCompletedAt(LocalDateTime.now());
        attemptRepo.save(attempt);

        // Update Enrollment with marks
        Enrollment enrollment = enrollmentRepo.findByStudentIDAndCourseID(
                attempt.getStudentId(),
                attempt.getExam().getCourse().getCourseid());

        if (enrollment == null) {
            throw new ResourceNotFoundException("Enrollment not found for student and course.");
        }

        enrollment.setMarks(percentage);
        enrollmentRepo.save(enrollment);

        activityLogService.logAction(
                attempt.getStudentId(),
                ActivityAction.EXAM_ATTEMPT,
                "Student " + attempt.getStudentId() + " submitted attempt " + attempt.getId() + " with score " + percentage + "%");

        return modelMapper.map(attempt, AttemptDTO.class);
    }

    @Override
    public AttemptDTO getAttemptById(Long attemptId) {
        Attempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        return modelMapper.map(attempt, AttemptDTO.class);
    }

    @Override
    public List<AttemptDTO> getAttemptsByExam(Long examId) {
        List<Attempt> attempts = attemptRepo.findByExamId(examId);
        return attempts.stream()
                .map(a -> modelMapper.map(a, AttemptDTO.class))
                .collect(Collectors.toList());
    }

}
