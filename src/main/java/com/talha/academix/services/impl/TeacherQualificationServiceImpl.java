// TeacherQualificationServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.TeacherQualificationDTO;
import com.talha.academix.enums.Degree;
import com.talha.academix.enums.Role;
import com.talha.academix.exception.ForbiddenException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.TeacherQualification;
import com.talha.academix.model.User;
import com.talha.academix.repository.TeacherQualificationRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.TeacherQualificationService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherQualificationServiceImpl implements TeacherQualificationService {
    private final TeacherQualificationRepo qualRepo;
    private final UserRepo userRepo;
    private final ModelMapper mapper;

    @Override
    public TeacherQualificationDTO addQualification(TeacherQualificationDTO dto) {
        User teacher = userRepo.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + dto.getTeacherId()));
        if (!teacher.getRole().equals(Role.TEACHER)) {
            throw new ForbiddenException("Only teacher can add qualification");
        }
        TeacherQualification qual = mapper.map(dto, TeacherQualification.class);
        qual.setTeacher(teacher);
        qual = qualRepo.save(qual);
        return mapper.map(qual, TeacherQualificationDTO.class);
    }

    @Override
    public TeacherQualificationDTO updateQualification(Long qualificationId, TeacherQualificationDTO dto) {
        TeacherQualification qual = qualRepo.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found: " + qualificationId));

        User teacher = userRepo.findById(dto.getTeacherId())
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + dto.getTeacherId()));

        if (!qualificationOwnedByTeacher(qualificationId, dto.getTeacherId())) {
            throw new ForbiddenException("Qualification does not belong to teacher with ID: " + dto.getTeacherId());
        }

        mapper.getConfiguration().setSkipNullEnabled(true);
        mapper.map(dto, qual);
        qual.setTeacher(teacher);
        qual = qualRepo.save(qual);
        return mapper.map(qual, TeacherQualificationDTO.class);
    }

    @Override
    public TeacherQualificationDTO getQualificationById(Long qualificationId) {
        TeacherQualification qual = qualRepo.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found: " + qualificationId));
        return mapper.map(qual, TeacherQualificationDTO.class);
    }

    @Override
    public List<TeacherQualificationDTO> getQualificationsByTeacher(Long teacherId) {
        User teacher = userRepo.findById(teacherId)
                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + teacherId));
        return qualRepo.findByTeacher(teacher).stream()
                .map(q -> mapper.map(q, TeacherQualificationDTO.class))
                .toList();
    }

    @Override
    public List<TeacherQualificationDTO> getQualificationsByDegree(Degree degree) {
        return qualRepo.findByDegree(degree) // if you convert string to enum earlier
                .stream()
                .map(q -> mapper.map(q, TeacherQualificationDTO.class))
                .toList();
    }

    @Override
    public void deleteQualification(Long qualificationId) {
        TeacherQualification qual = qualRepo.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found: " + qualificationId));
        qualRepo.delete(qual);
    }

    boolean qualificationOwnedByTeacher(Long qualificationId, Long teacherId) {
        TeacherQualification qual = qualRepo.findById(qualificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Qualification not found: " + qualificationId));
        return qual.getTeacher().getUserid().equals(teacherId);
    }
}
