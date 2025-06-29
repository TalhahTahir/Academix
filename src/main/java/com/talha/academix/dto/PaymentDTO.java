package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

import com.talha.academix.enums.PaymentMedium;
import com.talha.academix.enums.PaymentType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentDTO {
    private Long paymentID;
    private Long userID;
    private Long courseID;
    private Integer amount;
    private PaymentMedium medium;
    private PaymentType paymentType;
    private String account;

    private Date date;
}
