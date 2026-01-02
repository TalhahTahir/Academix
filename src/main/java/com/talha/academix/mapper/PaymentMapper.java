package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.PaymentDTO;
import com.talha.academix.model.Payment;

@Mapper(componentModel = "spring")
public interface PaymentMapper {
    
    @Mapping(source = "user.userid", target = "userId")
    @Mapping(source = "course.courseId", target = "courseId")
    PaymentDTO toDto(Payment payment);

    @Mapping(source = "userId", target = "user.userid")
    @Mapping(source = "courseId", target = "course.courseId")
    Payment toEntity(PaymentDTO dto);
}
