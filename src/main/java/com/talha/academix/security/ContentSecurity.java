package com.talha.academix.security;

import org.springframework.stereotype.Component;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Content;
import com.talha.academix.repository.ContentRepo;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component("contentSecurity")
public class ContentSecurity {

    private final ContentRepo contentRepo;

    public boolean isContentOwner(CustomUserDetails principal, Long contentId) {

        if (principal == null || contentId == null) {
            return false;
        }

        Content content = contentRepo.findById(contentId)
                .orElseThrow(() -> new ResourceNotFoundException("Content not found with id: " + contentId));

        return content.getCourse().getTeacher().getUserid().equals(principal.getId());
    }
}
