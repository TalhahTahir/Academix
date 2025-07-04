package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContentDTO {
    private Long contentID;
    private Long courseID;
    private String description;
    private String image;
}
