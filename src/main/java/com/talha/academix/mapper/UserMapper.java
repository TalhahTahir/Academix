package com.talha.academix.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.talha.academix.dto.CreateUserDTO;
import com.talha.academix.dto.UserDTO;
import com.talha.academix.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    
    UserDTO toDto(User user);

    User toEntity(UserDTO dto);

    User createUser(CreateUserDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(CreateUserDTO dto, @MappingTarget User u);
}
