package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentsDTO {
    private Long paymentID;
    private Long userID;
    private Long courseID;
    private Integer amount;
    private String method;
    private Date date;
}
