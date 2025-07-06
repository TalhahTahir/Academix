package com.talha.academix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {
    private Long contentID;
    private Long courseID;
    private String description;
    private String image;
    // Collections like lectures and documents are omitted
}
