package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.VaultDTO;
import com.talha.academix.model.Vault;

@Mapper(componentModel = "spring")
public interface VaultMapper {
    
    @Mapping(source = "user.userid", target = "userId")
    VaultDTO toDto(Vault vault);

    @Mapping(source = "userId", target = "user.userid")
    Vault toEntity(VaultDTO dto);
}
