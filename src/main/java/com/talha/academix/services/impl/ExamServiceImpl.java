package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.ExamDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
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
    private final ModelMapper modelMapper;

    @Override
    public ExamDTO createExam(Long teacherId, ExamDTO dto) {

        if (courseService.teacherValidation(teacherId, dto.getCourseId())) {

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

        if (courseService.teacherValidation(userid, existing.getCourse().getCourseid())) {
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

        if (courseService.teacherValidation(userid, exam.getCourse().getCourseid())) {
            examRepo.delete(exam);
        } else {
            throw new RoleMismatchException("Only teacher can delete exam");
        }
    }
}
