package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.QuestionOption;
import com.talha.academix.repository.OptionRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("optionSecurity")
public class OptionSecurity {
    
    private final OptionRepo optionRepo;

    public boolean isOptionOwner(CustomUserDetails principal, Long optionId) {
        if (principal == null || optionId == null) {
            return false;
        }

        QuestionOption option = optionRepo.findById(optionId).orElseThrow(() -> new ResourceNotFoundException("Option not found with id: " + optionId));
        return option.getQuestion().getExam().getCourse().getTeacher().getUserid().equals(principal.getId());
    }
}
