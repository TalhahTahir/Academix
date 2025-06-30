package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

import com.talha.academix.model.TeachingRequest.Status;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeachingRequestDTO {
    private Long requestId;
    private Long teacherId;
    private Long courseId;
    private Status status;
    private Date date;
}
