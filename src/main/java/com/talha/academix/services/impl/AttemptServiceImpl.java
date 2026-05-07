package com.talha.academix.services.impl;

import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.exception.AlreadyExistException;
import com.talha.academix.exception.BlankAnswerException;
import com.talha.academix.exception.ForbiddenException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.mapper.AttemptMapper;
import com.talha.academix.model.Attempt;
import com.talha.academix.model.AttemptAnswer;
import com.talha.academix.model.Enrollment;
import com.talha.academix.model.Exam;
import com.talha.academix.model.User;
import com.talha.academix.repository.AttemptAnswerRepo;
import com.talha.academix.repository.AttemptRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.repository.QuestionRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.AttemptService;
import com.talha.academix.services.EnrollmentService;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AttemptServiceImpl implements AttemptService {

    private final AttemptRepo attemptRepo;
    private final ExamRepo examRepo;
    private final AttemptAnswerRepo attemptAnswerRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final QuestionRepo questionRepo;
    private final EnrollmentService enrollmentService;
    private final UserRepo userRepo;
    private final AttemptMapper attemptMapper;

    @Override
    public AttemptDTO startAttempt(Long examId, Long studentId) {

        attemptRepo.findByExam_IdAndStudent_Userid(examId, studentId)
                .filter(a -> a.getCompletedAt() == null)
                .ifPresent(a -> {
                    throw new ForbiddenException("You have an ongoing attempt for this exam.");
                });
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        EnrollmentDTO stdEnrollment = enrollmentService.enrollmentValidation(exam.getCourse().getCourseId(), studentId);
        if (stdEnrollment.getCompletionPercentage() == 100) {
            User student = userRepo.findById(studentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + studentId));
            Attempt attempt = new Attempt();
            attempt.setExam(exam);
            attempt.setStudent(student);
            attempt.setStartedAt(Instant.now());
            attempt = attemptRepo.save(attempt);

            AttemptDTO dto = attemptMapper.toDto(attempt);
            return dto;
        } else {
            throw new ForbiddenException(
                    "You cannot start an attempt for this exam as your enrollment is not complete.");
        }
    }

    @Override
    @Transactional
    public AttemptDTO submitAttempt(Long attemptId, AttemptDTO dto) {
        Attempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));

        if (!attempt.getStudent().getUserid().equals(dto.getStudentId()))
            throw new ForbiddenException(
                    "You are not allowed to submit this attempt.");

        if (attempt.getCompletedAt() != null) {
            throw new AlreadyExistException("This attempt is already submitted.");
        }

        List<AttemptAnswer> answers = attemptAnswerRepo.findByAttemptId(attemptId);
        if (answers.isEmpty()) {
            throw new BlankAnswerException("Cannot submit an attempt without answers.");
        }

        long totalQuestions = questionRepo.countByExam_Id(attempt.getExam().getId());
        long correctAnswers = answers.stream()
                .filter(ans -> ans.getSelectedOption().isCorrect())
                .count();

        float percentage = ((float) correctAnswers / totalQuestions) * 100;

        attempt.setCompletedAt(Instant.now());
        attemptRepo.save(attempt);

        Enrollment enrollment = enrollmentRepo.findByStudent_UseridAndCourse_CourseId(
                attempt.getStudent().getUserid(),
                attempt.getExam().getCourse().getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Enrollment not found for student and course."));

        enrollment.setMarks(percentage);
        enrollmentRepo.save(enrollment);

        AttemptDTO out = attemptMapper.toDto(attempt);
        return out;
    }

    @Override
    public AttemptDTO getAttemptById(Long attemptId) {
        Attempt attempt = attemptRepo.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + attemptId));
        AttemptDTO dto = attemptMapper.toDto(attempt);
        return dto;
    }

    @Override
    public List<AttemptDTO> getAttemptsByExam(Long examId) {
        return attemptRepo.findByExamId(examId).stream()
                .map(a -> {
                    AttemptDTO d = attemptMapper.toDto(a);
                    d.setStudentId(a.getStudent().getUserid());
                    return d;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<AttemptDTO> getAttemptsByStudent(Long studentId) {
        return attemptRepo.findByStudentId(studentId).stream()
                .map(a -> {
                    AttemptDTO d = attemptMapper.toDto(a);
                    d.setStudentId(a.getStudent().getUserid());
                    return d;
                })
                .collect(Collectors.toList());
    }
}
