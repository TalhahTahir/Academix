package com.talha.academix.dto;

import java.util.Date;

import com.talha.academix.enums.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeachingRequestDTO {
    private Long requestId;
    private Long teacherId;
    private Long courseId;
    private RequestStatus status;
    private Date date;
}
