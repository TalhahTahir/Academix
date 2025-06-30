package com.talha.academix.services.impl;

import com.talha.academix.dto.TeacherQualificationDTO;
import com.talha.academix.enums.Degree;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.TeacherQualification;
import com.talha.academix.repository.TeacherQualificationRepo;
import com.talha.academix.services.TeacherQualificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeacherQualificationServiceImpl implements TeacherQualificationService {

    private final TeacherQualificationRepo qualificationRepo;
    private Degree degree;
    private final ModelMapper modelMapper;

    @Override
    public TeacherQualificationDTO addQualification(TeacherQualificationDTO dto) {
        TeacherQualification qualification = modelMapper.map(dto, TeacherQualification.class);
        qualification = qualificationRepo.save(qualification);
        return modelMapper.map(qualification, TeacherQualificationDTO.class);
    }

    @Override
    public TeacherQualificationDTO updateQualification(Long qualificationId, TeacherQualificationDTO dto) {
        TeacherQualification existing = qualificationRepo.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with id: " + qualificationId));
        existing.setTeacherId(dto.getTeacherId());
        existing.setDegree(dto.getDegree());
        existing.setInstitute(dto.getInstitute());
        existing.setYear(dto.getYear());
        existing = qualificationRepo.save(existing);
        return modelMapper.map(existing, TeacherQualificationDTO.class);
    }

    @Override
    public TeacherQualificationDTO getQualificationById(Long qualificationId) {
        TeacherQualification qualification = qualificationRepo.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with id: " + qualificationId));
        return modelMapper.map(qualification, TeacherQualificationDTO.class);
    }

    @Override
    public List<TeacherQualificationDTO> getQualificationsByTeacher(Long teacherId) {
        List<TeacherQualification> qualifications = qualificationRepo.findByTeacherID(teacherId);
        return qualifications.stream()
                .map(q -> modelMapper.map(q, TeacherQualificationDTO.class))
                .toList();
    }

    @Override
    public List<TeacherQualificationDTO> getQualificationsByDegree(Degree degree) {
        List<TeacherQualification> qualifications = qualificationRepo.findByDegree(degree);
        return qualifications.stream()
                .map(q -> modelMapper.map(q, TeacherQualificationDTO.class))
                .toList();
    }

    @Override
    public void deleteQualification(Long qualificationId) {
        TeacherQualification qualification = qualificationRepo.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found with id: " + qualificationId));
        qualificationRepo.delete(qualification);
    }
}
