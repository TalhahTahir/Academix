package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.CourseDTO;
import com.talha.academix.dto.ExamDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Course;
import com.talha.academix.model.Exam;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.services.ExamService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {

    private final ExamRepo examRepo;
    private final CourseRepo courseRepo;
    private final ModelMapper modelMapper;

    @Override
    public ExamDTO addExam(ExamDTO dto) {
        Exam exam = modelMapper.map(dto, Exam.class);
        exam = examRepo.save(exam);
        return modelMapper.map(exam, ExamDTO.class);
        }

     @Override
    public ExamDTO updateExam(Long examId, ExamDTO dto) {
       Exam existing = examRepo.findById(examId)
       .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id " + examId));
       existing.setCourseId(dto.getCourseId());
       existing.setQuestions(dto.getQuestions());
       existing = examRepo.save(existing);
       return modelMapper.map(existing, ExamDTO.class);
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
    public void deleteExam(Long examId) {
        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        examRepo.delete(exam);
    }



    @Override
    public void deleteExamByTeacher(Long teacherId, Long examId) {
        
        Long courseId = courseRepo.findContentIdByExamId(examId);
        TeacherAuth(teacherId, courseId);

        Exam exam = examRepo.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id: " + examId));
        examRepo.delete(exam);
    }

     public boolean TeacherAuth(Long teacherId, Long contentId) {
        Course course = courseRepo.findByContentID(contentId);
        if (course.getTeacherid() != teacherId) {
            throw new RoleMismatchException("Unauthorized Teacher");
        }
        return true;
    }

     @Override
     public ExamDTO createExamByTeacher(Long teacherId, ExamDTO dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createExamByTeacher'");
     }

     @Override
     public ExamDTO updateExamByTeacher(Long teacherId, Long examId, ExamDTO dto) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'updateExamByTeacher'");
     }
    
}
