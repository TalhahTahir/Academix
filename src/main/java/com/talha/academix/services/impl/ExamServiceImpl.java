package com.talha.academix.services.impl;

import java.util.List;
import java.util.Objects;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.AttemptDTO;
import com.talha.academix.dto.EnrollmentDTO;
import com.talha.academix.dto.ExamDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Attempt;
import com.talha.academix.model.AttemptAnswer;
import com.talha.academix.model.Exam;
import com.talha.academix.model.Option;
import com.talha.academix.model.Question;
import com.talha.academix.repository.AttemptRepo;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.EnrollmentService;
import com.talha.academix.services.ExamService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepo examRepo;
    private final CourseRepo courseRepo;
    private final CourseService courseService;
    private final AttemptRepo attemptRepo;
    private final EnrollmentService enrollmentService;
    private final ActivityLogService activityLogService;
    private final ModelMapper modelMapper;

    @Override
    public ExamDTO createExam(Long teacherId, ExamDTO dto) {

        if (courseService.teacherOwnership(teacherId, dto.getCourseId())) {

            Exam exam = modelMapper.map(dto, Exam.class);
            exam.setCourse(courseRepo.findById(dto.getCourseId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found")));
            exam = examRepo.save(exam);

            return modelMapper.map(exam, ExamDTO.class);
        } else
            throw new RoleMismatchException("only Teacher can create exam");
    }

    @Override
    public ExamDTO updateExam(Long userid, Long examId, ExamDTO dto) {
        Exam existing = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (courseService.teacherOwnership(userid, existing.getCourse().getCourseid())) {
            existing.setTitle(dto.getTitle());
            examRepo.save(existing);

            return modelMapper.map(existing, ExamDTO.class);
        } else {
            throw new RoleMismatchException("Only teacher can update exam");
        }
    }

    @Override
    public ExamDTO getExamById(Long examId) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        return modelMapper.map(exam, ExamDTO.class);
    }

    @Override
    public List<ExamDTO> getExamsByCourse(Long courseId) {
        List<Exam> exams = examRepo.findByCourseId(courseId);
        return exams.stream()
                .map(e -> modelMapper.map(e, ExamDTO.class))
                .toList();
    }

    @Override
    public void deleteExam(Long userid, Long examId) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));

        if (courseService.teacherOwnership(userid, exam.getCourse().getCourseid())) {
            examRepo.delete(exam);
        } else {
            throw new RoleMismatchException("Only teacher can delete exam");
        }
    }

    @Override
    public Double checkExam(AttemptDTO dto) {
    
        Exam exam = examRepo.findById(dto.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + dto.getExamId()));
    
        Attempt attempt = attemptRepo.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with id: " + dto.getId()));
    
        EnrollmentDTO enrollment = enrollmentService.enrollmentValidation(
                exam.getCourse().getCourseid(), dto.getStudentId());
        if (enrollment == null) {
            throw new ResourceNotFoundException("Enrollment not found for student and course.");
        }
    
        List<AttemptAnswer> answers = attempt.getAnswers();
        if (answers.isEmpty()) {
            throw new IllegalStateException("Cannot check an attempt without answers.");
        }
    
        int marksCount = 0;
    
        for (AttemptAnswer answer : answers) {
            Question question = answer.getQuestion();
            Long correctOption = null; 
    
            for (Option option : question.getOptions()) {
                if (option.isCorrect()) {
                    correctOption = option.getId();
                    break; 
                }
            }
    
            if (Objects.equals(answer.getSelectedOption().getId(), correctOption)) {
                marksCount++;
            }
        }
    
        double percentage = (double) marksCount / answers.size() * 100;
    
        enrollment.setMarks(percentage);
        enrollmentService.updateEnrollment(enrollment);
    
        activityLogService.logAction(
            dto.getStudentId(), ActivityAction.EXAM_ATTEMPT,
            "Checked exam, scored: " + percentage + "%"
        );
    
        return percentage;
    }
    
}
