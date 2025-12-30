package com.talha.academix.services.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.model.TeacherAccount;
import com.talha.academix.repository.TeacherAccountRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherAccountPersistenceService {

    private final TeacherAccountRepo teacherAccountRepo;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TeacherAccount saveNew(TeacherAccount ta) {
        return teacherAccountRepo.save(ta);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public TeacherAccount saveUpdate(TeacherAccount ta) {
        return teacherAccountRepo.save(ta);
    }
}