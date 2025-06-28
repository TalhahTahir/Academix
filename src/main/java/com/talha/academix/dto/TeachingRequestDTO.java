package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeachingRequestDTO {
    private Long requestId;
    private Long teacherId;
    private Long courseId;
    private String status;
    private Date date;
}
